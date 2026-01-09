package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.WorkStateEnum;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.domain.SocialMediaWorkTask;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.service.SocialMediaWorkTaskService;
import com.chargehub.common.core.constant.CacheConstants;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import com.google.common.collect.Lists;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
public abstract class AbstractSyncWorkScheduler {

    public static final String WEB_RESOURCE_PATH = "/opt/resources/crawler";


    public final SocialMediaWorkTaskService socialMediaWorkTaskService;

    public final RedisService redisService;


    public final DataSyncManager dataSyncManager;


    public final SocialMediaWorkService socialMediaWorkService;

    public final SocialMediaWorkCreateService socialMediaWorkCreateService;

    public final HubProperties hubProperties;

    public ThreadPoolExecutor fixedThreadPool;

    @Setter
    public String taskName;

    protected AbstractSyncWorkScheduler(SocialMediaWorkTaskService socialMediaWorkTaskService,
                                        RedisService redisService,
                                        DataSyncManager dataSyncManager,
                                        SocialMediaWorkService socialMediaWorkService,
                                        SocialMediaWorkCreateService socialMediaWorkCreateService,
                                        HubProperties hubProperties,
                                        Integer threads) {
        this.socialMediaWorkTaskService = socialMediaWorkTaskService;
        this.redisService = redisService;
        this.dataSyncManager = dataSyncManager;
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
        List<SocialMediaWorkTask> tasks = socialMediaWorkTaskService.getAllByPlatformId(taskName, limit);
        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }
        DateTime now = DateUtil.date();
        List<String> completeIds = new CopyOnWriteArrayList<>();
        List<CompletableFuture<Void>> allFutures = new ArrayList<>();
        com.microsoft.playwright.options.Proxy browserProxy = PlaywrightBrowser.buildProxy();
        List<List<SocialMediaWorkTask>> partition = Lists.partition(tasks, 5);
        for (List<SocialMediaWorkTask> list : partition) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.fetchWorks(list, completeIds, null, browserProxy), fixedThreadPool);
            allFutures.add(future);
        }
        StopWatch stopWatch = new StopWatch(taskName);
        stopWatch.start();
        log.info("{}开始同步作品 {}", taskName, now);
        redisService.setHashEx(CacheConstants.SYNCING_WORK_LOCK, taskName, now.toString(), 2, TimeUnit.HOURS);
        try {
            CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).get(2, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("{}同步作品异常 {}", taskName, e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            redisService.deleteCacheMapValue(CacheConstants.SYNCING_WORK_LOCK, taskName);
            stopWatch.stop();
            log.info("{}同步作品结束 {}秒", taskName, stopWatch.getTotalTimeSeconds());
        }
    }


    public void fetchWorks(List<SocialMediaWorkTask> tasks, List<String> completeIds, Proxy proxy, com.microsoft.playwright.options.Proxy browserProxy) {
        List<String> ids = tasks.stream().map(SocialMediaWorkTask::getWorkId).collect(Collectors.toList());
        try (PlaywrightBrowser playwrightBrowser = getPlaywrightBrowser(browserProxy)) {
            List<SocialMediaWork> list = this.socialMediaWorkService.getWorkByIds(ids);
            if (CollectionUtils.isEmpty(list)) {
                this.socialMediaWorkTaskService.deleteTaskByIds(ids);
                return;
            }
            Map<String, SocialMediaWork> workMap = new HashMap<>();
            for (SocialMediaWork socialMediaWork : list) {
                workMap.put(socialMediaWork.getWorkUid(), socialMediaWork);
            }
            DataSyncWorksParams dataSyncWorksParams = new DataSyncWorksParams();
            dataSyncWorksParams.setWorkMap(workMap);
            dataSyncWorksParams.setProxy(proxy);
            dataSyncWorksParams.setPlaywrightBrowser(playwrightBrowser);
            SocialMediaWorkResult<SocialMediaWork> result = this.dataSyncManager.fetchWorks(this.taskName, dataSyncWorksParams);
            List<SocialMediaWork> newWorks = result.getWorks();
            if (CollectionUtils.isEmpty(newWorks)) {
                return;
            }
            List<SocialMediaWork> updateList = new ArrayList<>();
            for (SocialMediaWork newWork : newWorks) {
                String workUid = newWork.getWorkUid();
                if ("-1".equals(workUid)) {
                    String shareLink = newWork.getShareLink();
                    log.error("删除作品分享链接: {}", shareLink);
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
            this.socialMediaWorkService.updateBatchById(updateList);
            this.socialMediaWorkTaskService.deleteTaskByIds(ids);
            completeIds.addAll(ids);
        } catch (Exception e) {
            log.error("{} 同步任务异常,作品: {}", taskName, String.join(StringPool.COMMA, ids), e);
        }
    }

    public PlaywrightBrowser getPlaywrightBrowser(com.microsoft.playwright.options.Proxy browserProxy) {
        return new PlaywrightBrowser(browserProxy);
    }


}
