package com.chargehub.admin.scheduler;

import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.DataSyncWorksParams;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.enums.SyncWorkStatusEnum;
import com.chargehub.admin.enums.WorkStateEnum;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.playwright.PlaywrightCrawlHelper;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Component
public class DouYinWorkScheduler extends AbstractWorkScheduler {

    @Autowired
    private PlaywrightCrawlHelper playwrightCrawlHelper;


    protected DouYinWorkScheduler(SocialMediaAccountTaskService socialMediaAccountTaskService, RedisService redisService, DataSyncManager dataSyncManager, SocialMediaAccountService socialMediaAccountService, SocialMediaWorkService socialMediaWorkService, SocialMediaWorkCreateService socialMediaWorkCreateService, HubProperties hubProperties) {
        super(socialMediaAccountTaskService, redisService, dataSyncManager, socialMediaAccountService, socialMediaWorkService, socialMediaWorkCreateService, hubProperties, 4);
        this.setTaskName(SocialMediaPlatformEnum.DOU_YIN.getDomain());

    }

    public void fetchWorks(SocialMediaAccount socialMediaAccountVo, Proxy proxy) {
        String accountId = socialMediaAccountVo.getId();
        String platformId = socialMediaAccountVo.getPlatformId();
        String secUid = socialMediaAccountVo.getSecUid();
        List<SocialMediaWork> latestWork = this.socialMediaWorkService.getLatestWork(accountId);
        if (CollectionUtils.isEmpty(latestWork)) {
            return;
        }
        DataSyncWorksParams dataSyncWorksParams = new DataSyncWorksParams();
        com.microsoft.playwright.options.Proxy browserProxy = PlaywrightBrowser.buildProxy();
        Playwright playwright = null;
        BrowserContext browserContext = null;
        String crawlerLoginState = playwrightCrawlHelper.getCrawlerLoginState(SocialMediaPlatformEnum.DOU_YIN.getDomain());
        boolean hasVideo = latestWork.stream().anyMatch(i -> i.getMediaType().equals(MediaTypeEnum.VIDEO.getType()));
        if (hasVideo) {
            playwright = Playwright.create();
            browserContext = PlaywrightBrowser.buildBrowserContext(crawlerLoginState, playwright, browserProxy);
        }
        try {
            this.socialMediaAccountService.updateSyncWorkStatus(accountId, SyncWorkStatusEnum.SYNCING);
            Map<String, SocialMediaWork> workMap = new HashMap<>();
            for (SocialMediaWork socialMediaWork : latestWork) {
                workMap.put(socialMediaWork.getWorkUid(), socialMediaWork);
            }
            dataSyncWorksParams.setSecUid(secUid);
            dataSyncWorksParams.setWorkMap(workMap);
            dataSyncWorksParams.setProxy(proxy);
            dataSyncWorksParams.setBrowserContext(browserContext);
            dataSyncWorksParams.setStorageState(crawlerLoginState);
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
        } finally {
            if (playwright != null) {
                browserContext.close();
                playwright.close();
            }
        }

    }


}
