package com.chargehub.thirdparty.api.domain.vo.wx.ma;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:08
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.wx.ma
 * @Filename：WxMaJscode2SessionResultVo
 */
@Data
public class WxMaJscode2SessionResultVo {


    /**
     * 会话密钥
     */
    private String sessionKey;

    /**
     * 用户在当前小程序的标识符
     */
    private String openid;

    /**
     * unionid
     */
    private String unionid;
}
