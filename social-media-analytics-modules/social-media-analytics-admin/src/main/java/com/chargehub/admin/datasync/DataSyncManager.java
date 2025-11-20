package com.chargehub.admin.datasync;

import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
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

    public <T> SocialMediaWorkResult<T> getWorks(SocialMediaAccountVo socialMediaAccount, Integer cursor, Integer count) {
        String platformId = socialMediaAccount.getPlatformId();
        SocialMediaPlatformEnum platform = SocialMediaPlatformEnum.getByDomain(platformId);
        DataSyncService dataSyncService = SERVICES.get(platform);
        Assert.notNull(dataSyncService, "不支持的数据同步平台");
        return dataSyncService.getWorks(socialMediaAccount, cursor, count);
    }

}
