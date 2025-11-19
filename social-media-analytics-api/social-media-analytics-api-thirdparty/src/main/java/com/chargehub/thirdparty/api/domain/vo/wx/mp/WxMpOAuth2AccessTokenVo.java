package com.chargehub.thirdparty.api.domain.vo.wx.mp;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:22
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.wx.mp
 * @Filename：WxMpOAuth2AccessTokenVo
 */
@Data
public class WxMpOAuth2AccessTokenVo {

    private String accessToken;

    private String refreshToken;

    private String openId;

    private String scope;

    private String unionId;
}
