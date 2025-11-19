package com.chargehub.thirdparty.api.domain.vo.wx.open;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 14:39
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.dto.wx.open
 * @Filename：WxOpenUserInfoVo
 */
@Data
public class WxOpenUserInfoVo {

    /**
     * 获取结果
     * true:成功 false:失败
     */
    private Boolean result;

    /**
     * 错误提示
     */
    private String msg;

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
