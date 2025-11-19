package com.chargehub.thirdparty.api.domain.vo.wx.mp;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:58
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.wx.mp
 * @Filename：WxMpConfigInfoVo
 */
@Data
public class WxMpConfigInfoVo {

    /**
     * 微信小程序appId
     */
    private String appId;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 签名
     */
    private String signature;
}
