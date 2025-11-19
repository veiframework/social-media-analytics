package com.chargehub.admin.datasync.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaUserInfo implements Serializable {
    private static final long serialVersionUID = -3326578766093434135L;

    private String nickname;

    private String uid;



}
