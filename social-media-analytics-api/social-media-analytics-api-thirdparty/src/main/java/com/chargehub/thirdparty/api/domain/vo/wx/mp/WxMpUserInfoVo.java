package com.chargehub.thirdparty.api.domain.vo.wx.mp;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:29
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.wx.mp
 * @Filename：WxMpUserInfoVo
 */
@Data
public class WxMpUserInfoVo {

    private Boolean subscribe;
    private String openId;
    private String nickname;
    private String sexDesc;
    private Integer sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headImgUrl;
    private Long subscribeTime;
    private String unionId;
    private String remark;
    private Integer groupId;
    private Long[] tagIds;
    private String[] privileges;
    private String subscribeScene;
    private String qrScene;
    private String qrSceneStr;
}
