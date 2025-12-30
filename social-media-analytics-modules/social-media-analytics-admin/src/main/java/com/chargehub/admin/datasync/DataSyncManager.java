package com.chargehub.admin.datasync;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.domain.*;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.scheduler.DouYinWorkScheduler;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
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

    public <T> SocialMediaWorkDetail<T> fetchWork(String shareLink, SocialMediaPlatformEnum.PlatformExtra platformExtra) {
        DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
        SocialMediaPlatformEnum platform = platformExtra.getPlatformEnum();
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        String location = platformExtra.getLocation();
        dataSyncParamContext.setRedirectUrl(location);
        dataSyncParamContext.setShareLink(shareLink);
        dataSyncParamContext.setProxy(BrowserConfig.getProxy());
        if (platform != SocialMediaPlatformEnum.DOU_YIN) {
            return dataSyncService.fetchWork(dataSyncParamContext);
        }
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(StringPool.EMPTY)) {
            DouYinWorkScheduler.navigateToDouYinUserPage(playwrightBrowser);
            dataSyncParamContext.setPage(playwrightBrowser.getPage());
            return dataSyncService.fetchWork(dataSyncParamContext);
        }
    }

    public <T> SocialMediaWorkResult<T> fetchWorks(String platformId, DataSyncWorksParams dataSyncWorksParams) {
        SocialMediaPlatformEnum platform = SocialMediaPlatformEnum.getByDomain(platformId);
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        return dataSyncService.fetchWorks(dataSyncWorksParams);
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
