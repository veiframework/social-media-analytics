package com.chargehub.admin.datasync;

import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.domain.DataSyncParamContext;
import com.chargehub.admin.datasync.domain.SocialMediaDetail;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.datasync.domain.SocialMediaWorkResult;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
public interface DataSyncService {

    SocialMediaPlatformEnum platform();

    SocialMediaUserInfo getSocialMediaUserInfo(String secUserId);

    <T> SocialMediaWorkResult<T> getWorks(SocialMediaAccountVo socialMediaAccount, String cursor, Integer count);

    default SocialMediaDetail getSecUidByWorkUrl(String url) {
        return null;
    }


    default <T> T getWork(String workUid) {
        return null;
    }

    default <T> T getWork(DataSyncParamContext context) {
        return null;
    }

}
