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

    @Deprecated
    SocialMediaUserInfo getSocialMediaUserInfo(String secUserId);

    <T> SocialMediaWorkResult<T> getWorks(SocialMediaAccountVo socialMediaAccount, String cursor, Integer count);

    /**
     * v3版本数据同步
     * 返回的作品list如果是空则不更新cookie
     *
     * @param params
     * @param <T>
     * @return
     */
    <T> SocialMediaWorkResult<T> getWorks(DataSyncWorksParams params);


    <T> SocialMediaWorkDetail<T> getWork(DataSyncParamContext dataSyncParamContext);

    <T> SocialMediaWorkDetail<T> fetchWork(DataSyncParamContext dataSyncParamContext);


}
