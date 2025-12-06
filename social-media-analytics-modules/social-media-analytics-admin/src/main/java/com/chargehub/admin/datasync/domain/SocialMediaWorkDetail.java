package com.chargehub.admin.datasync.domain;

import lombok.Data;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaWorkDetail<T> {

    private T work;

    private SocialMediaUserInfo socialMediaUserInfo;


    public SocialMediaWorkDetail(T work, SocialMediaUserInfo socialMediaUserInfo) {
        this.work = work;
        this.socialMediaUserInfo = socialMediaUserInfo;
    }
}
