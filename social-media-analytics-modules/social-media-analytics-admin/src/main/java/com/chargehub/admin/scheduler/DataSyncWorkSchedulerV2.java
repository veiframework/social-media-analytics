package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.DataSyncMessageQueue;
import com.chargehub.admin.datasync.domain.DataSyncParamContext;
import com.chargehub.admin.datasync.domain.SocialMediaWorkDetail;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.playwright.PlaywrightCrawlHelper;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Deprecated
@Slf4j
@Component("dataSyncWorkSchedulerV2")
public class DataSyncWorkSchedulerV2 {

    public static final String LOCK_WORK_KEY = "lock:work:sync:";


    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private DataSyncManager dataSyncManager;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PlaywrightCrawlHelper playwrightCrawlHelper;


    private static final ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(10);


    public void asyncExecute(Set<String> accountId) {
        FIXED_THREAD_POOL.execute(() -> this.execute(accountId));
    }

    public void execute() {
        this.execute(null);
    }

    public void execute(Set<String> accountId) {
        StopWatch stopWatch = new StopWatch("作品同步任务");
        try {
            stopWatch.start();
            log.info("作品同步任务开始v2 {}", DateUtil.now());
            List<SocialMediaWork> workIds = this.socialMediaWorkService.getWorkIds(accountId);
            if (CollectionUtils.isEmpty(workIds)) {
                return;
            }
            Map<String, List<SocialMediaWork>> collect = workIds.stream().collect(Collectors.groupingBy(SocialMediaWork::getPlatformId));
            List<CompletableFuture<Void>> allFutures = new ArrayList<>();
            collect.forEach((platform, works) -> {
                String crawlerLoginState = this.playwrightCrawlHelper.getCrawlerLoginState(platform);
                Playwright playwright = Playwright.create();
                BrowserContext browserContext = PlaywrightBrowser.buildBrowserContext(crawlerLoginState, playwright);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    if (platform.equals(SocialMediaPlatformEnum.DOU_YIN.getDomain())) {
                        this.douYinFetchWorks(works, browserContext, crawlerLoginState, playwright);
                    } else {
                        this.fetchWorks(works, browserContext, crawlerLoginState, playwright);
                    }
                }, DataSyncMessageQueue.FIXED_THREAD_POOL);
                allFutures.add(future);
            });
            // 等待所有任务完成
            if (allFutures.isEmpty()) {
                return;
            }
            CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
        } finally {
            stopWatch.stop();
            log.info("作品同步任务v2结束 {}秒", stopWatch.getTotalTimeSeconds());
        }
    }

    private void douYinFetchWorks(List<SocialMediaWork> works, BrowserContext browserContext, String crawlerLoginState, Playwright playwright) {
        Playwright douYinPictuerPlaywright = Playwright.create();
        BrowserContext douYinPictureBrowser = PlaywrightBrowser.buildBrowserContext(null, douYinPictuerPlaywright);
        try {
            if (CollectionUtils.isEmpty(works)) {
                return;
            }
            String platformId = works.get(0).getPlatformId();
            String storageState = null;
            for (SocialMediaWork work : works) {
                if (work.getMediaType().equals(MediaTypeEnum.PICTURE.getType())) {
                    this.fetchWork(work.getId(), douYinPictureBrowser, crawlerLoginState);
                    storageState = crawlerLoginState;
                } else {
                    storageState = this.fetchWork(work.getId(), browserContext, crawlerLoginState);
                }
            }
            this.playwrightCrawlHelper.updateCrawlerLoginState(platformId, storageState);
        } finally {
            browserContext.close();
            playwright.close();
            douYinPictureBrowser.close();
            douYinPictuerPlaywright.close();
        }
    }

    private void fetchWorks(List<SocialMediaWork> works, BrowserContext browserContext, String crawlerLoginState, Playwright playwright) {
        try {
            if (CollectionUtils.isEmpty(works)) {
                return;
            }
            String platformId = works.get(0).getPlatformId();
            String storageState = null;
            for (SocialMediaWork work : works) {
                storageState = this.fetchWork(work.getId(), browserContext, crawlerLoginState);
            }
            this.playwrightCrawlHelper.updateCrawlerLoginState(platformId, storageState);
        } finally {
            browserContext.close();
            playwright.close();
        }
    }

    private String fetchWork(String workId, BrowserContext browserContext, String crawlerLoginState) {
        return redisService.lock(LOCK_WORK_KEY + workId, locked -> {
            if (BooleanUtils.isFalse(locked)) {
                log.debug("发现作品同步任务正在执行 {}", workId);
                return crawlerLoginState;
            }
            SocialMediaWork vo = this.socialMediaWorkService.getDomainById(workId);
            if (vo == null) {
                return crawlerLoginState;
            }
            long betweenMs = DateUtil.betweenMs(vo.getSyncWorkDate(), new Date());
            if (betweenMs <= 1000 * 60 * 30) {
                log.debug("发现作品同步任务最近执行过 {}-{}", workId, betweenMs);
                return crawlerLoginState;
            }
            String accountId = vo.getAccountId();
            String platformId = vo.getPlatformId();
            SocialMediaPlatformEnum platformEnum = SocialMediaPlatformEnum.getByDomain(platformId);
            String shareLink = vo.getUrl();
            if (platformEnum == SocialMediaPlatformEnum.WECHAT_VIDEO) {
                shareLink = vo.getWorkUid();
            }
            try {
                this.socialMediaWorkService.updateSyncWorkStatus(workId, SyncWorkStatusEnum.SYNCING);
                DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
                dataSyncParamContext.setShareLink(shareLink);
                dataSyncParamContext.setBrowserContext(browserContext);
                dataSyncParamContext.setAccountId(accountId);
                dataSyncParamContext.setScheduler(true);
                SocialMediaWorkDetail<SocialMediaWork> socialMediaWorkDetail = this.dataSyncManager.getWork(dataSyncParamContext, platformEnum);
                if (socialMediaWorkDetail == null) {
                    this.socialMediaWorkService.updateSyncWorkStatus(workId, SyncWorkStatusEnum.COMPLETE);
                    return crawlerLoginState;
                }
                SocialMediaWork socialMediaWork = socialMediaWorkDetail.getWork();
                socialMediaWork.setAccountId(vo.getAccountId());
                socialMediaWork.setTenantId(vo.getTenantId());
                socialMediaWork.setAccountType(vo.getType());
                SocialMediaWork updateWork = vo.computeMd5(socialMediaWork);
                if (updateWork != null) {
                    updateWork.setSyncWorkDate(new Date());
                    updateWork.setSyncWorkStatus(SyncWorkStatusEnum.COMPLETE.ordinal());
                    this.socialMediaWorkService.updateOne(updateWork);
                } else {
                    this.socialMediaWorkService.updateSyncWorkStatus(workId, SyncWorkStatusEnum.COMPLETE);
                }
                return dataSyncParamContext.getStorageState();
            } catch (Exception e) {
                log.error(workId + ":作品同步任务异常", e);
                this.socialMediaWorkService.updateSyncWorkStatus(workId, SyncWorkStatusEnum.ERROR);
                return crawlerLoginState;
            }
        });
    }

}
