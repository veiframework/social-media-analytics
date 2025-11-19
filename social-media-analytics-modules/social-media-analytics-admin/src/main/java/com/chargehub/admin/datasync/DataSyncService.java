package com.chargehub.admin.datasync;

import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
public interface DataSyncService {

    SocialMediaPlatformEnum platform();

    SocialMediaUserInfo getSocialMediaUserInfo(String secUserId);



}
