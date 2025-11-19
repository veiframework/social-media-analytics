package com.chargehub.thirdparty.api.domain.vo.wx.open;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:08
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.wx.open
 * @Filename：WxOpenMaJscode2SessionVo
 */
@Data
public class WxOpenMaJscode2SessionVo {

    /**
     * 获取结果
     * true:成功 false:失败
     */
    private Boolean result;

    /**
     * 错误提示
     */
    private String msg;

    private String sessionKey;
    private String openid;
    private String unionid;
}
