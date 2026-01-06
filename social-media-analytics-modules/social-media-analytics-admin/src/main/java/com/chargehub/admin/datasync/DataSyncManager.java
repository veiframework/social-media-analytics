package com.chargehub.admin.datasync;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.domain.*;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.scheduler.DouYinWorkScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.Proxy;
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


    public <T> SocialMediaWorkDetail<T> fetchWork(String shareLink, SocialMediaPlatformEnum.PlatformExtra platformExtra) {
        DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
        SocialMediaPlatformEnum platform = platformExtra.getPlatformEnum();
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        String location = platformExtra.getLocation();
        Proxy proxy = BrowserConfig.getProxy();
        dataSyncParamContext.setRedirectUrl(location);
        dataSyncParamContext.setShareLink(shareLink);
        dataSyncParamContext.setProxy(proxy);
        if (platform != SocialMediaPlatformEnum.DOU_YIN) {
            return dataSyncService.fetchWork(dataSyncParamContext);
        }
        com.microsoft.playwright.options.Proxy browserProxy = PlaywrightBrowser.buildProxy();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(browserProxy)) {
            DouYinWorkScheduler.navigateToDouYinUserPage(playwrightBrowser, proxy);
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
