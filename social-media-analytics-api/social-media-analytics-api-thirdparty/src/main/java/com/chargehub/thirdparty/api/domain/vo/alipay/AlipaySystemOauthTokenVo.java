package com.chargehub.thirdparty.api.domain.vo.alipay;

import lombok.Data;

import java.util.Date;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 17:22
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.alipay
 * @Filename：AlipaySystemOauthTokenVo
 */
@Data
public class AlipaySystemOauthTokenVo {
    
    private String accessToken;

    private String alipayUserId;

    private Date authStart;

    private String authTokenType;

    private String expiresIn;

    private String reExpiresIn;

    private String refreshToken;

    private String userId;

    private String openId;
}
