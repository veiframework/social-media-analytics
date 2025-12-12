package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.AutoSyncEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
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
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.*;

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
        this.execute(accountIds, false);
    }

    public void execute() {
        this.execute(null, true);
    }

    public void execute(Set<String> accountIds, boolean wait) {
        SocialMediaAccountQueryDto socialMediaAccountQueryDto = new SocialMediaAccountQueryDto();
        socialMediaAccountQueryDto.setCrawler(0);
        socialMediaAccountQueryDto.setAutoSync(AutoSyncEnum.ENABLE.getDesc());
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
        redisService.lock("add-task-lock", locked -> {
            Assert.isTrue(locked, "当前操作人数过多请稍等");
            Integer maxAccountNum = this.socialMediaAccountService.getMaxAccountNum();
            int targetTaskSize = CollectionUtils.isNotEmpty(accountIds) ? accountIds.size() : maxAccountNum;
            int queueSize = FIXED_THREAD_POOL.getQueue().size();
            int activeCount = FIXED_THREAD_POOL.getActiveCount();
            int totalPendingTasks = queueSize + activeCount + targetTaskSize;
            if (totalPendingTasks > maxAccountNum) {
                throw new IllegalArgumentException("当前正在同步的账号数量超过" + maxAccountNum + "个,请稍后操作!");
            }
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
            return null;
        }, 30);
        StopWatch stopWatch = new StopWatch("作品同步任务");
        stopWatch.start();
        log.info("作品同步任务v3开始 {}", DateUtil.now());
        Runnable runnable = () -> {
            try {
                CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).get(1, TimeUnit.HOURS);
                newStorageStateMap.forEach((platform, storageState) -> this.playwrightCrawlHelper.updateCrawlerLoginState(platform, storageState.getLoginState()));
                log.info("cookie更新完毕...");
            } catch (Exception e) {
                log.error("作品同步任务v3超时 {}", e.getMessage());
                Thread.currentThread().interrupt();
            } finally {
                stopWatch.stop();
                log.info("作品同步任务v3结束 {}秒", stopWatch.getTotalTimeSeconds());
            }
        };
        if (wait) {
            runnable.run();
            return;
        }
        ThreadHelper.execute(runnable);
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
                if (!platformId.equals(SocialMediaPlatformEnum.RED_NOTE.getDomain())) {
                    newStorageStateMap.put(platformId, new UpdateLoginState(storageState));
                }
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
