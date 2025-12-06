package com.chargehub.admin.datasync;

import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.domain.*;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
public interface DataSyncService {

    SocialMediaPlatformEnum platform();

    SocialMediaUserInfo getSocialMediaUserInfo(String secUserId);

    <T> SocialMediaWorkResult<T> getWorks(SocialMediaAccountVo socialMediaAccount, String cursor, Integer count);


    <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext);

}
