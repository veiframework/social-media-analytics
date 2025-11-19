package com.chargehub.thirdparty.api.domain.vo.wx.ma;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:12
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.wx.ma
 * @Filename：WxMaUserInfoVo
 */
@Data
public class WxMaUserInfoVo {

    private String openId;
    private String nickName;
    private String gender;
    private String language;
    private String city;
    private String province;
    private String country;
    private String avatarUrl;
    private String unionId;
}
