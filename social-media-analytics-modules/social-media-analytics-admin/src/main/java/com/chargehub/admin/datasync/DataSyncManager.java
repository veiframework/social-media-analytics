package com.chargehub.admin.datasync;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.domain.*;
import com.chargehub.admin.enums.MediaTypeEnum;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.playwright.PlaywrightCrawlHelper;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Slf4j
@Component
public class DataSyncManager {

    private static final Map<SocialMediaPlatformEnum, DataSyncService> SERVICES = new EnumMap<>(SocialMediaPlatformEnum.class);

    private static final boolean TEST = false;

    @Autowired
    private PlaywrightCrawlHelper playwrightCrawlHelper;


    public DataSyncManager(List<DataSyncService> services) {
        for (DataSyncService service : services) {
            SERVICES.put(service.platform(), service);
        }
    }

    public SocialMediaUserInfo getSocialMediaUserInfo(SocialMediaPlatformEnum platform, String secUserId) {
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        return dataSyncService.getSocialMediaUserInfo(secUserId);
    }

    public <T> SocialMediaWorkResult<T> getWorks(SocialMediaAccountVo socialMediaAccount, String cursor, Integer count) {
        String platformId = socialMediaAccount.getPlatformId();
        SocialMediaPlatformEnum platform = SocialMediaPlatformEnum.getByDomain(platformId);
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        return dataSyncService.getWorks(socialMediaAccount, cursor, count);
    }

    public <T> SocialMediaWorkDetail<T> getWork(String accountId, String shareLink, SocialMediaPlatformEnum.PlatformExtra platformExtra) {
        DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
        SocialMediaPlatformEnum platform = platformExtra.getPlatformEnum();
        String location = platformExtra.getLocation();
        dataSyncParamContext.setRedirectUrl(location);
        Playwright playwright = Playwright.create();
        BrowserContext browserContext = PlaywrightBrowser.buildBrowserContext(dataSyncParamContext.getStorageState(), playwright);
        try {
            dataSyncParamContext.setAccountId(accountId);
            dataSyncParamContext.setShareLink(shareLink);
            dataSyncParamContext.setBrowserContext(browserContext);
            return this.getWork(dataSyncParamContext, platform);
        } finally {
            browserContext.close();
            playwright.close();
        }
    }

    public <T> SocialMediaWorkDetail<T> fetchWork(String accountId, String shareLink, SocialMediaPlatformEnum.PlatformExtra platformExtra) {
        DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
        SocialMediaPlatformEnum platform = platformExtra.getPlatformEnum();
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        String location = platformExtra.getLocation();
        dataSyncParamContext.setRedirectUrl(location);
        dataSyncParamContext.setAccountId(accountId);
        dataSyncParamContext.setShareLink(shareLink);
        dataSyncParamContext.setProxy(BrowserConfig.getProxy());
        if (platform == SocialMediaPlatformEnum.DOU_YIN) {
            String crawlerLoginState = playwrightCrawlHelper.getCrawlerLoginState(platform.getDomain());
            com.microsoft.playwright.options.Proxy browserProxy = PlaywrightBrowser.buildProxy();
            Playwright playwright = Playwright.create();
            BrowserContext browserContext = PlaywrightBrowser.buildBrowserContext(crawlerLoginState, playwright, browserProxy);
            try {
                dataSyncParamContext.setStorageState(crawlerLoginState);
                dataSyncParamContext.setBrowserContext(browserContext);
                return dataSyncService.fetchWork(dataSyncParamContext);
            } finally {
                browserContext.close();
                playwright.close();
            }
        }
        return dataSyncService.fetchWork(dataSyncParamContext);
    }

    @SuppressWarnings("unchecked")
    public <T> SocialMediaWorkResult<T> fetchWorks(String platformId, DataSyncWorksParams dataSyncWorksParams) {
        SocialMediaPlatformEnum platform = SocialMediaPlatformEnum.getByDomain(platformId);
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        BrowserContext browserContext = dataSyncWorksParams.getBrowserContext();
        Map<String, SocialMediaWork> workMap = dataSyncWorksParams.getWorkMap();
        SocialMediaWorkResult<SocialMediaWork> socialMediaWorkResult = new SocialMediaWorkResult<>();
        List<SocialMediaWork> socialMediaWorks = new ArrayList<>();
        workMap.forEach((k, v) -> {
            String mediaType = v.getMediaType();
            DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
            dataSyncParamContext.setShareLink(v.getShareLink());
            dataSyncParamContext.setBrowserContext(browserContext);
            dataSyncParamContext.setScheduler(true);
            dataSyncParamContext.setMediaType(mediaType);
            dataSyncParamContext.setProxy(dataSyncWorksParams.getProxy());
            dataSyncParamContext.setStorageState(dataSyncWorksParams.getStorageState());
            try {
                SocialMediaWorkDetail<SocialMediaWork> workDetail = dataSyncService.fetchWork(dataSyncParamContext);
                if (workDetail == null) {
                    return;
                }
                socialMediaWorks.add(workDetail.getWork());
            } catch (Exception e) {
                log.error("{}获取作品失败, {}", v.getShareLink(), e);
            }
        });
        socialMediaWorkResult.setWorks(socialMediaWorks);
        return (SocialMediaWorkResult<T>) socialMediaWorkResult;
    }


    public <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext, SocialMediaPlatformEnum platform) {
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        return dataSyncService.getWork(dataSyncParamContext);
    }

    public <T> SocialMediaWorkResult<T> getWorks(String platformId, DataSyncWorksParams dataSyncWorksParams) {
        if (TEST) {
            ThreadUtil.safeSleep(RandomUtil.randomInt(5_000, 30_000));
            SocialMediaWorkResult<T> result = new SocialMediaWorkResult<>();
            result.setWorks(new ArrayList<>());
            return result;
        }
        SocialMediaPlatformEnum platform = SocialMediaPlatformEnum.getByDomain(platformId);
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        return dataSyncService.getWorks(dataSyncWorksParams);
    }
}
