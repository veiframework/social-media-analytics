package com.chargehub.thirdparty.api.domain.vo.wx.open;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 14:27
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.dto.wx.open
 * @Filename：WxOpenOAuth2AccessTokenVo
 */
@Data
public class WxOpenOAuth2AccessTokenVo {

    /**
     * 获取结果
     * true:成功 false:失败
     */
    private Boolean result;

    /**
     * 错误提示
     */
    private String msg;


    /**
     * 获取到的凭证
     */
    private String accessToken;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * 用户微信openId
     */
    private String openId;

    /**
     * 范围
     */
    private String scope;

    /**
     * 用户微信unionId
     */
    private String unionId;
}
