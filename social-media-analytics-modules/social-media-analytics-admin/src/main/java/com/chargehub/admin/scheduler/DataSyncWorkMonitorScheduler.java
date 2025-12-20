package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.domain.SocialMediaAccountTask;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.playwright.PlaywrightCrawlHelper;
import com.chargehub.admin.scheduler.domain.UpdateLoginState;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.redis.service.RedisService;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Component
public class DataSyncWorkMonitorScheduler {

    private static final String DATA_SYNC_WORK_LOCK = "DATA_SYNC_WORK_LOCK";

    public static final String SYNCING_WORK_LOCK = "SYNCING_WORK_LOCK";

    @Autowired
    private SocialMediaAccountTaskService socialMediaAccountTaskService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PlaywrightCrawlHelper playwrightCrawlHelper;

    @Autowired
    private DataSyncManager dataSyncManager;

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;


    private static final ThreadPoolExecutor FIXED_THREAD_POOL = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    public void execute() {
        redisService.lock(DATA_SYNC_WORK_LOCK, locked -> {
            if (BooleanUtils.isFalse(locked)) {
                return null;
            }
            List<SocialMediaAccountTask> socialMediaAccounts = socialMediaAccountTaskService.getAll();
            if (CollectionUtils.isEmpty(socialMediaAccounts)) {
                return null;
            }
            Map<String, String> crawlerLoginStateMap = this.playwrightCrawlHelper.getCrawlerLoginStateMap();
            Map<String, UpdateLoginState> newStorageStateMap = new ConcurrentHashMap<>();
            List<CompletableFuture<Void>> allFutures = new ArrayList<>();
            for (SocialMediaAccountTask socialMediaAccount : socialMediaAccounts) {
                String accountId = socialMediaAccount.getAccountId();
                String platformId = socialMediaAccount.getPlatformId();
                String crawlerLoginState = crawlerLoginStateMap.get(platformId);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.fetchWorks(accountId, crawlerLoginState, newStorageStateMap), FIXED_THREAD_POOL);
                allFutures.add(future);
            }
            StopWatch stopWatch = new StopWatch("作品同步任务");
            stopWatch.start();
            String now = DateUtil.now();
            log.info("作品同步任务v4开始 {}", now);
            redisService.setCacheObject(SYNCING_WORK_LOCK, now);
            try {
                CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).get(1, TimeUnit.HOURS);
                newStorageStateMap.forEach((platform, storageState) -> this.playwrightCrawlHelper.updateCrawlerLoginState(platform, storageState.getLoginState()));
            } catch (Exception e) {
                log.error("作品同步任务v4超时 {}", e.getMessage());
                Thread.currentThread().interrupt();
            } finally {
                stopWatch.stop();
                log.info("作品同步任务v4结束 {}秒", stopWatch.getTotalTimeSeconds());
                redisService.deleteObject(SYNCING_WORK_LOCK);
            }
            return null;
        });
    }

    private void fetchWorks(String accountId, String crawlerLoginState, Map<String, UpdateLoginState> newStorageStateMap) {
        SocialMediaAccount socialMediaAccountVo = this.socialMediaAccountService.getById(accountId);
        if (socialMediaAccountVo == null) {
            log.error(accountId + "账号已被删除");
            this.socialMediaAccountTaskService.deleteTaskById(accountId);
            return;
        }
        if (!socialMediaAccountVo.computeSyncDuration(new Date(), 30)) {
            log.error(accountId + "最近同步过了,不再执行");
            this.socialMediaAccountTaskService.deleteTaskById(accountId);
            return;
        }
        Playwright playwright = Playwright.create();
        BrowserContext browserContext = PlaywrightBrowser.buildBrowserContext(crawlerLoginState, playwright);
        try {
            this.fetchWorks(socialMediaAccountVo, browserContext, newStorageStateMap);
            this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.COMPLETE);
            this.socialMediaAccountTaskService.deleteTaskById(accountId);
        } catch (Exception e) {
            log.error("账号: " + accountId + "作品同步任务v3异常", e);
        } finally {
            browserContext.close();
            playwright.close();
        }
    }

    @SuppressWarnings("java:S3776")
    private void fetchWorks(SocialMediaAccount socialMediaAccountVo, BrowserContext browserContext, Map<String, UpdateLoginState> newStorageStateMap) {
        String accountId = socialMediaAccountVo.getId();
        String platformId = socialMediaAccountVo.getPlatformId();
        String secUid = socialMediaAccountVo.getSecUid();
        String accountType = socialMediaAccountVo.getType();
        String userId = socialMediaAccountVo.getUserId();
        String tenantId = socialMediaAccountVo.getTenantId();
        List<SocialMediaWork> latestWork = this.socialMediaWorkService.getLatestWork(accountId);
        if (CollectionUtils.isEmpty(latestWork) && !platformId.equals(SocialMediaPlatformEnum.WECHAT_VIDEO.getDomain())) {
            return;
        }
        this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.SYNCING);
        Map<String, String> workUids = new HashMap<>();
        Map<String, SocialMediaWork> workMap = new HashMap<>();
        for (SocialMediaWork socialMediaWork : latestWork) {
            workUids.put(socialMediaWork.getWorkUid(), socialMediaWork.getShareLink());
            workMap.put(socialMediaWork.getWorkUid(), socialMediaWork);
        }
        DataSyncWorksParams dataSyncWorksParams = new DataSyncWorksParams();
        dataSyncWorksParams.setSecUid(secUid);
        dataSyncWorksParams.setBrowserContext(browserContext);
        dataSyncWorksParams.setWorkUids(workUids);
        SocialMediaWorkResult<SocialMediaWork> result = this.dataSyncManager.getWorks(platformId, dataSyncWorksParams);
        List<SocialMediaWork> newWorks = result.getWorks();
        if (CollectionUtils.isEmpty(newWorks)) {
            return;
        }
        List<SocialMediaWork> updateList = new ArrayList<>();
        for (SocialMediaWork newWork : newWorks) {
            String workUid = newWork.getWorkUid();
            if ("-1".equals(workUid)) {
                String shareLink = newWork.getShareLink();
                log.error("删除作品分享链接:" + shareLink);
                this.socialMediaWorkService.deleteByShareLink(shareLink);
            } else {
                SocialMediaWork existWork = workMap.get(workUid);
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
        }
        this.socialMediaWorkService.saveOrUpdateBatch(updateList);
        String storageState = result.getStorageState();
        newStorageStateMap.put(platformId, new UpdateLoginState(storageState));
    }


}
