package com.chargehub.thirdparty.api.domain.vo.wx.open;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 14:51
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.dto.wx.open
 * @Filename：OpenAuthUrlVo
 */
@Data
public class WxOpenAuthUrlVo {

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
     * 微信开放平台授权跳转地址
     */
    private String authUrl;
}
