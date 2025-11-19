package com.chargehub.auth.domain.dto;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/15 14:20
 * @Project：chargehub
 * @Package：com.chargehub.auth.form
 * @Filename：CompleteUserInfoDto
 */
@Data
public class CompleteUserInfoDto {

    //微信小程序openId
    private String wxMaOpenId;

    //微信/支付宝 unionId
    private String unionId;

    //支付宝用户id
    private String aliUserId;

    //支付宝用户openId
    private String aliOpenid;

    //百度openId
    private String baiduOpenid;
}
