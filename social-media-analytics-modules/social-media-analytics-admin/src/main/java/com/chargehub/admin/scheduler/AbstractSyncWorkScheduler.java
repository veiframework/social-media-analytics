package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.WorkPriorityEnum;
import com.chargehub.admin.enums.WorkStateEnum;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.domain.SocialMediaWorkPriority;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkPriorityService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.service.SocialMediaWorkTaskService;
import com.chargehub.common.core.constant.CacheConstants;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import com.google.common.collect.Lists;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.Proxy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private SocialMediaWorkPriorityService socialMediaWorkPriorityService;

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
        Set<String> ids = redisService.getCacheSet(SocialMediaWorkTaskService.DEFAULT_SYNC_WORK_TASK + taskName, limit);
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        Map<Integer, SocialMediaWorkPriority> allPriority = socialMediaWorkPriorityService.getAllPriority();
        LocalDateTime now = LocalDateTime.now();
        List<CompletableFuture<Void>> allFutures = new ArrayList<>();
        com.microsoft.playwright.options.Proxy browserProxy = PlaywrightBrowser.buildProxy();
        List<String> idList = new ArrayList<>(ids);
        List<List<String>> partition = Lists.partition(idList, 5);
        for (List<String> list : partition) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.fetchWorks(now, list, null, browserProxy, allPriority), fixedThreadPool);
            allFutures.add(future);
        }
        StopWatch stopWatch = new StopWatch(taskName);
        stopWatch.start();
        String formatDate = now.format(DatePattern.NORM_DATETIME_FORMATTER);
        log.info("{}开始同步作品 {}", taskName, formatDate);
        redisService.setHashEx(CacheConstants.SYNCING_WORK_LOCK, taskName, formatDate, 2, TimeUnit.HOURS);
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


    public void fetchWorks(LocalDateTime now, List<String> ids, Proxy proxy, com.microsoft.playwright.options.Proxy browserProxy, Map<Integer, SocialMediaWorkPriority> allPriority) {
        Map<String, Double> nextCrawTimeCache = new HashMap<>();
        try (PlaywrightBrowser playwrightBrowser = getPlaywrightBrowser(browserProxy)) {
            List<SocialMediaWork> list = this.socialMediaWorkService.getWorkByIds(ids);
            if (CollectionUtils.isEmpty(list)) {
                redisService.deleteCacheSet(SocialMediaWorkTaskService.DEFAULT_SYNC_WORK_TASK + taskName, ids);
                return;
            }
            Map<String, SocialMediaWork> workMap = new HashMap<>();
            Map<String, String> workLinkIdMap = new HashMap<>();
            for (SocialMediaWork socialMediaWork : list) {
                workMap.put(socialMediaWork.getWorkUid(), socialMediaWork);
                workLinkIdMap.put(socialMediaWork.getShareLink(), socialMediaWork.getId());
            }
            DataSyncWorksParams dataSyncWorksParams = new DataSyncWorksParams();
            dataSyncWorksParams.setWorkMap(workMap);
            dataSyncWorksParams.setProxy(proxy);
            dataSyncWorksParams.setPlaywrightBrowser(playwrightBrowser);
            SocialMediaWorkResult<SocialMediaWork> result = this.dataSyncManager.fetchWorks(this.taskName, dataSyncWorksParams);
            List<SocialMediaWork> newWorks = result.getWorks();
            if (CollectionUtils.isEmpty(newWorks)) {
                redisService.deleteCacheSet(SocialMediaWorkTaskService.DEFAULT_SYNC_WORK_TASK + taskName, ids);
                return;
            }
            List<SocialMediaWork> updateList = new ArrayList<>();
            for (SocialMediaWork newWork : newWorks) {
                String workUid = newWork.getWorkUid();
                if ("-1".equals(workUid)) {
                    String shareLink = newWork.getShareLink();
                    log.error("删除作品分享链接: {}", shareLink);
                    this.socialMediaWorkService.updateStateByShareLink(shareLink, WorkStateEnum.DELETED);
                    String workId = workLinkIdMap.get(shareLink);
                    if (StringUtils.isNotBlank(workId)) {
                        redisService.deleteZSet(CacheConstants.WORK_NEXT_CRAWL_TIME, workId);
                    } else {
                        log.error("从缓存中删除分享链接未找到: {}", shareLink);
                    }
                } else {
                    SocialMediaWork existWork = workMap.get(workUid);
                    if (existWork == null) {
                        continue;
                    }
                    String id = existWork.getId();
                    Date updateTime = existWork.getUpdateTime();
                    SocialMediaWork updateWork = existWork.computeMd5(newWork);
                    Integer priority;
                    boolean isChanged = updateWork != null;
                    if (isChanged) {
                        priority = SocialMediaWorkPriorityService.computePriority(now, updateTime, updateWork, allPriority);
                        updateWork.setPriority(priority);
                        updateList.add(updateWork);
                    } else {
                        priority = SocialMediaWorkPriorityService.computePriority(now, updateTime, existWork, allPriority);
                        if (!existWork.getPriority().equals(priority)) {
                            //优先级变化了才更新
                            SocialMediaWork work = new SocialMediaWork();
                            work.setId(id);
                            work.setPriority(priority);
                            updateList.add(work);
                        }
                    }
                    if (priority == WorkPriorityEnum.DOCUMENT.getCode()) {
                        //归档类型暂时先删除
                        redisService.deleteZSet(CacheConstants.WORK_NEXT_CRAWL_TIME, id);
                    } else {
                        SocialMediaWorkPriority currentPriority = allPriority.get(priority);
                        Integer interval = isChanged ? currentPriority.getBacklogInterval() : currentPriority.getNormalInterval();
                        double nextCrawlTime = Long.parseLong(now.plusSeconds(interval).format(DatePattern.PURE_DATETIME_FORMATTER));
                        nextCrawTimeCache.put(id, nextCrawlTime);
                    }
                }
            }
            this.socialMediaWorkService.updateBatchById(updateList);
            //更新下次采集时间
            redisService.addZSetMembers(CacheConstants.WORK_NEXT_CRAWL_TIME, nextCrawTimeCache);
            redisService.deleteCacheSet(SocialMediaWorkTaskService.DEFAULT_SYNC_WORK_TASK + taskName, ids);
        } catch (Exception e) {
            log.error("{} 同步任务异常,作品: {}", taskName, String.join(StringPool.COMMA, ids), e);
        }
    }

    public PlaywrightBrowser getPlaywrightBrowser(com.microsoft.playwright.options.Proxy browserProxy) {
        return new PlaywrightBrowser(browserProxy);
    }


}
