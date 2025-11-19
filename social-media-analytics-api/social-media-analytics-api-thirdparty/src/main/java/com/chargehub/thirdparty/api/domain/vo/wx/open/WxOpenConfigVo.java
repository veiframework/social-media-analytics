package com.chargehub.thirdparty.api.domain.vo.wx.open;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 14:55
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.dto.wx.open
 * @Filename：WxOpenConfigVo
 */
@Data
public class WxOpenConfigVo {

    /**
     * 获取结果
     * true:成功 false:失败
     */
    private Boolean result;

    /**
     * 错误提示
     */
    private String msg;

    private String appId;
    private String timestamp;
    private String nonceStr;
    private String signature;
}
