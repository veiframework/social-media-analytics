package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.domain.SocialMediaAccountTask;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.enums.WorkStateEnum;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.net.Proxy;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
public abstract class AbstractWorkScheduler {

    public static final String WEB_RESOURCE_PATH = "/opt/resources/crawler";

    public static final String SYNCING_WORK_LOCK = "SYNCING_WORK_LOCK";

    public final SocialMediaAccountTaskService socialMediaAccountTaskService;

    public final RedisService redisService;


    public final DataSyncManager dataSyncManager;

    public final SocialMediaAccountService socialMediaAccountService;

    public final SocialMediaWorkService socialMediaWorkService;

    public final SocialMediaWorkCreateService socialMediaWorkCreateService;

    public final HubProperties hubProperties;

    public ThreadPoolExecutor fixedThreadPool;

    @Setter
    public String taskName;

    protected AbstractWorkScheduler(SocialMediaAccountTaskService socialMediaAccountTaskService,
                                    RedisService redisService,
                                    DataSyncManager dataSyncManager,
                                    SocialMediaAccountService socialMediaAccountService,
                                    SocialMediaWorkService socialMediaWorkService,
                                    SocialMediaWorkCreateService socialMediaWorkCreateService,
                                    HubProperties hubProperties,
                                    Integer threads) {
        this.socialMediaAccountTaskService = socialMediaAccountTaskService;
        this.redisService = redisService;
        this.dataSyncManager = dataSyncManager;
        this.socialMediaAccountService = socialMediaAccountService;
        this.socialMediaWorkService = socialMediaWorkService;
        this.socialMediaWorkCreateService = socialMediaWorkCreateService;
        this.hubProperties = hubProperties;
        if (threads == null) {
            return;
        }
        this.fixedThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
    }

    public void execute(Integer limit) {
        boolean hasTask = socialMediaWorkCreateService.hasTask();
        if (hasTask) {
            log.error("正在创建作品, {}作品同步不执行", taskName);
            return;
        }
        List<SocialMediaAccountTask> socialMediaAccounts = socialMediaAccountTaskService.getAllByPlatformId(taskName, limit);
        if (CollectionUtils.isEmpty(socialMediaAccounts)) {
            return;
        }
        DateTime now = DateUtil.date();
        List<String> completeAccountIds = new CopyOnWriteArrayList<>();
        List<CompletableFuture<Void>> allFutures = new ArrayList<>();
        Proxy proxy = BrowserConfig.getProxy();
        com.microsoft.playwright.options.Proxy browserProxy = PlaywrightBrowser.buildProxy();
        for (SocialMediaAccountTask socialMediaAccount : socialMediaAccounts) {
            String accountId = socialMediaAccount.getAccountId();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.fetchWorks(now, accountId, completeAccountIds, proxy, browserProxy), fixedThreadPool);
            allFutures.add(future);
        }
        StopWatch stopWatch = new StopWatch(taskName);
        stopWatch.start();
        log.info("{}开始同步作品 {}", taskName, now);
        redisService.setHashEx(SYNCING_WORK_LOCK, taskName, now.toString(), 2, TimeUnit.HOURS);
        try {
            CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).get(2, TimeUnit.HOURS);
            Date endDate = new Date();
            List<SocialMediaAccount> completeList = completeAccountIds.stream().map(i -> {
                SocialMediaAccount socialMediaAccount = new SocialMediaAccount();
                socialMediaAccount.setId(i);
                socialMediaAccount.setSyncWorkDate(endDate);
                socialMediaAccount.setSyncWorkStatus(SyncWorkStatusEnum.COMPLETE.ordinal());
                return socialMediaAccount;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(completeList)) {
                Db.updateBatchById(completeList);
            }
        } catch (Exception e) {
            log.error("{}同步作品异常 {}", taskName, e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            redisService.deleteCacheMapValue(SYNCING_WORK_LOCK, taskName);
            stopWatch.stop();
            log.info("{}同步作品结束 {}秒", taskName, stopWatch.getTotalTimeSeconds());
        }
    }

    public void fetchWorks(Date now, String accountId, List<String> completeAccountIds, Proxy proxy, com.microsoft.playwright.options.Proxy browserProxy) {
        SocialMediaAccount socialMediaAccountVo = this.socialMediaAccountService.getById(accountId);
        if (socialMediaAccountVo == null) {
            log.error(accountId + "账号已被删除");
            this.socialMediaAccountTaskService.deleteTaskById(accountId);
            return;
        }
        Integer updateMinutes = hubProperties.getUpdateMinutes();
        if (!socialMediaAccountVo.computeSyncDuration(now, updateMinutes)) {
            log.error(accountId + "最近同步过了,不再执行");
            this.socialMediaAccountTaskService.deleteTaskById(accountId);
            return;
        }
        try {
            this.fetchWorks(socialMediaAccountVo, proxy, browserProxy);
            this.socialMediaAccountTaskService.deleteTaskById(accountId);
            completeAccountIds.add(accountId);
        } catch (Exception e) {
            log.error(taskName + "同步任务异常,账号: " + accountId, e);
        }
    }

    public void fetchWorks(SocialMediaAccount socialMediaAccountVo, Proxy proxy, com.microsoft.playwright.options.Proxy browserProxy) {
        String accountId = socialMediaAccountVo.getId();
        String platformId = socialMediaAccountVo.getPlatformId();
        String secUid = socialMediaAccountVo.getSecUid();
        List<SocialMediaWork> latestWork = this.socialMediaWorkService.getLatestWork(accountId);
        if (CollectionUtils.isEmpty(latestWork)) {
            return;
        }
        this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.SYNCING);
        Map<String, SocialMediaWork> workMap = new HashMap<>();
        for (SocialMediaWork socialMediaWork : latestWork) {
            workMap.put(socialMediaWork.getWorkUid(), socialMediaWork);
        }
        DataSyncWorksParams dataSyncWorksParams = new DataSyncWorksParams();
        dataSyncWorksParams.setSecUid(secUid);
        dataSyncWorksParams.setWorkMap(workMap);
        dataSyncWorksParams.setProxy(proxy);
        SocialMediaWorkResult<SocialMediaWork> result = this.dataSyncManager.fetchWorks(platformId, dataSyncWorksParams);
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
                this.socialMediaWorkService.updateStateByShareLink(shareLink, WorkStateEnum.DELETED);
            } else {
                SocialMediaWork existWork = workMap.get(workUid);
                if (existWork == null) {
                    continue;
                }
                SocialMediaWork updateWork = existWork.computeMd5(newWork);
                if (updateWork != null) {
                    updateList.add(updateWork);
                }
            }
        }
        this.socialMediaWorkService.saveOrUpdateBatch(updateList);
    }

}
