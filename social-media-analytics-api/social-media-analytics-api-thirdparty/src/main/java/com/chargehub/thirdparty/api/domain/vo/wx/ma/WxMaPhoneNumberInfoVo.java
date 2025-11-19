package com.chargehub.thirdparty.api.domain.vo.wx.ma;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:15
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.wx.ma
 * @Filename：WxMaPhoneNumberInfoVo
 */
@Data
public class WxMaPhoneNumberInfoVo {

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 纯数字手机号
     */
    private String purePhoneNumber;

    /**
     * 国家区域编号
     */
    private String countryCode;
}
