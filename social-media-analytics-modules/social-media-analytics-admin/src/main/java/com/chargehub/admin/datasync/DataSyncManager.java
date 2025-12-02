package com.chargehub.admin.datasync;

import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.domain.DataSyncParamContext;
import com.chargehub.admin.datasync.domain.SocialMediaDetail;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.playwright.PlaywrightCrawlHelper;
import com.chargehub.admin.work.domain.SocialMediaWork;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Component
public class DataSyncManager {

    private static final Map<SocialMediaPlatformEnum, DataSyncService> SERVICES = new EnumMap<>(SocialMediaPlatformEnum.class);

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

    public SocialMediaDetail getSecUidByWorkUrl(String url) {
        SocialMediaPlatformEnum platform = SocialMediaPlatformEnum.getPlatformByWorkUrl(url);
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        return dataSyncService.getSecUidByWorkUrl(url);
    }

    public SocialMediaDetail getSecUidByWorkUrl(SocialMediaPlatformEnum platform, String url) {
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        return dataSyncService.getSecUidByWorkUrl(url);
    }

    public SocialMediaWork getWork(String accountId, String workUid, String platformId) {
        String loginState = playwrightCrawlHelper.getLoginState(accountId);
        if (StringUtils.isBlank(loginState)) {
            return null;
        }
        SocialMediaPlatformEnum platform = SocialMediaPlatformEnum.getByDomain(platformId);
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        DataSyncParamContext dataSyncParamContext = new DataSyncParamContext();
        dataSyncParamContext.setWorkUid(workUid);
        dataSyncParamContext.setAccountId(accountId);
        dataSyncParamContext.setStorageState(loginState);
        SocialMediaWork work = dataSyncService.getWork(dataSyncParamContext);
        //更新登录状态
        playwrightCrawlHelper.saveLoginState(accountId, dataSyncParamContext.getStorageState());
        return work;
    }

    public SocialMediaWork getWork(DataSyncParamContext dataSyncParamContext, String platformId) {
        String storageState = dataSyncParamContext.getStorageState();
        if (StringUtils.isBlank(storageState)) {
            return null;
        }
        SocialMediaPlatformEnum platform = SocialMediaPlatformEnum.getByDomain(platformId);
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        SocialMediaWork work = dataSyncService.getWork(dataSyncParamContext);
        //更新登录状态
        playwrightCrawlHelper.saveLoginState(dataSyncParamContext.getAccountId(), storageState);
        return work;
    }

    public SocialMediaWork getWork(String platformId, String workUid) {
        SocialMediaPlatformEnum platform = SocialMediaPlatformEnum.getByDomain(platformId);
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        return dataSyncService.getWork(workUid);
    }

}
