package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.playwright.PlaywrightCrawlHelper;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.core.utils.ThreadHelper;
import com.chargehub.common.redis.service.RedisService;
import com.google.common.collect.Sets;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Component("dataSyncWorkSchedulerV3")
public class DataSyncWorkSchedulerV3 {

    public static final String SYNC_ACCOUNT_LOCK = "sync_account_lock:";

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private DataSyncManager dataSyncManager;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PlaywrightCrawlHelper playwrightCrawlHelper;


    private static final ThreadPoolExecutor FIXED_THREAD_POOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    public void asyncExecute(Set<String> accountIds) {
        ThreadHelper.execute(() -> this.execute(accountIds));
    }

    public void execute() {
        this.execute(null);
    }

    public void execute(Set<String> accountIds) {
        StopWatch stopWatch = new StopWatch("作品同步任务");
        try {
            stopWatch.start();
            log.info("作品同步任务v3开始 {}", DateUtil.now());
            SocialMediaAccountQueryDto socialMediaAccountQueryDto = new SocialMediaAccountQueryDto();
            socialMediaAccountQueryDto.setCrawler(0);
            socialMediaAccountQueryDto.setSyncWorkStatus(Sets.newHashSet(SyncWorkStatusEnum.WAIT.ordinal(), SyncWorkStatusEnum.COMPLETE.ordinal(), SyncWorkStatusEnum.ERROR.ordinal()));
            if (CollectionUtils.isNotEmpty(accountIds)) {
                socialMediaAccountQueryDto.setId(accountIds);
            }
            List<SocialMediaAccount> socialMediaAccounts = this.socialMediaAccountService.getAccountIds(socialMediaAccountQueryDto);
            if (CollectionUtils.isEmpty(socialMediaAccounts)) {
                return;
            }
            Map<String, String> crawlerLoginStateMap = this.playwrightCrawlHelper.getCrawlerLoginStateMap();
            Map<String, UpdateLoginState> newStorageStateMap = new ConcurrentHashMap<>();
            List<CompletableFuture<Void>> allFutures = new ArrayList<>();
            for (SocialMediaAccount socialMediaAccount : socialMediaAccounts) {
                String accountId = socialMediaAccount.getId();
                String platformId = socialMediaAccount.getPlatformId();
                String crawlerLoginState = crawlerLoginStateMap.get(platformId);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    Playwright playwright = Playwright.create();
                    BrowserContext browserContext = PlaywrightBrowser.buildBrowserContext(crawlerLoginState, playwright);
                    try {
                        this.fetchWorks(accountId, browserContext, newStorageStateMap);
                    } finally {
                        browserContext.close();
                        playwright.close();
                    }
                }, FIXED_THREAD_POOL);
                allFutures.add(future);
            }
            CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
            log.info("开始更新cookie...");
            newStorageStateMap.forEach((platform, storageState) -> this.playwrightCrawlHelper.updateCrawlerLoginState(platform, storageState.getLoginState()));
            log.info("cookie更新完毕...");
        } finally {
            stopWatch.stop();
            log.info("作品同步任务v3结束 {}秒", stopWatch.getTotalTimeSeconds());
        }
    }

    private void fetchWorks(String accountId, BrowserContext browserContext, Map<String, UpdateLoginState> newStorageStateMap) {
        redisService.lock(SYNC_ACCOUNT_LOCK + accountId, locked -> {
            try {
                if (BooleanUtils.isFalse(locked)) {
                    log.warn("发现账号同步任务正在执行 {}", accountId);
                    return null;
                }
                SocialMediaAccount socialMediaAccountVo = this.socialMediaAccountService.getById(accountId);
                String platformId = socialMediaAccountVo.getPlatformId();
                String secUid = socialMediaAccountVo.getSecUid();
                Date syncWorkDate = socialMediaAccountVo.getSyncWorkDate();
                String accountType = socialMediaAccountVo.getType();
                String userId = socialMediaAccountVo.getUserId();
                String tenantId = socialMediaAccountVo.getTenantId();
                Integer syncWorkStatus = socialMediaAccountVo.getSyncWorkStatus();

                if (syncWorkDate != null && !syncWorkStatus.equals(SyncWorkStatusEnum.ERROR.ordinal())) {
                    long betweenMs = DateUtil.betweenMs(syncWorkDate, new Date());
                    if (betweenMs <= 1000 * 60 * 30) {
                        log.warn("发现账号同步最近同步过 {}-{}", accountId, betweenMs);
                        return null;
                    }
                }

                List<SocialMediaWork> latestWork = this.socialMediaWorkService.getLatestWork(accountId);
                if (CollectionUtils.isEmpty(latestWork)) {
                    return null;
                }
                this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.SYNCING);


                Map<String, String> workUids = new HashMap<>();
                Map<String, SocialMediaWork> workMap = new HashMap<>();
                for (SocialMediaWork socialMediaWork : latestWork) {
                    workUids.put(socialMediaWork.getWorkUid(), socialMediaWork.getUrl());
                    workMap.put(socialMediaWork.getWorkUid(), socialMediaWork);
                }
                DataSyncWorksParams dataSyncWorksParams = new DataSyncWorksParams();
                dataSyncWorksParams.setSecUid(secUid);
                dataSyncWorksParams.setBrowserContext(browserContext);
                dataSyncWorksParams.setWorkUids(workUids);
                SocialMediaWorkResult<SocialMediaWork> result = this.dataSyncManager.getWorks(platformId, dataSyncWorksParams);
                List<SocialMediaWork> newWorks = result.getWorks();
                if (CollectionUtils.isEmpty(newWorks)) {
                    this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.COMPLETE);
                    return null;
                }

                List<SocialMediaWork> updateList = new ArrayList<>();
                for (SocialMediaWork newWork : newWorks) {
                    SocialMediaWork existWork = workMap.get(newWork.getWorkUid());
                    if (existWork == null) {
                        newWork.setUserId(userId);
                        newWork.setAccountId(accountId);
                        newWork.setTenantId(tenantId);
                        newWork.setAccountType(accountType);
                        newWork.setPlatformId(platformId);
                        updateList.add(newWork);
                    } else {
                        SocialMediaWork updateWork = existWork.computeMd5(newWork);
                        if (updateWork != null) {
                            updateList.add(updateWork);
                        }
                    }
                }
                String storageState = result.getStorageState();
                this.socialMediaWorkService.saveOrUpdateBatch(updateList);
                this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.COMPLETE);
                newStorageStateMap.put(platformId, new UpdateLoginState(storageState));
                return null;
            } catch (Exception e) {
                log.error("账号: " + accountId + "作品同步任务v3异常", e);
                this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.ERROR);
                return null;
            }
        });
    }


    @Data
    public static class UpdateLoginState {
        private String loginState;

        public UpdateLoginState(String loginState) {
            this.loginState = loginState;
        }
    }

}
