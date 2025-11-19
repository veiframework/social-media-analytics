package com.chargehub.thirdparty.api.domain.vo.logistics;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/1/30 13:49
 * @Project：vc-shoes-server
 * @Package：com.vchaoxi.wx.open.vo.logistics
 * @Filename：LoAccountBindVo
 */
@Data
public class LoOrderCancelVo {

    private Integer errcode;
    private String errmsg;

    /**
     * 运力返回的错误码
     */
    private Integer delivery_resultcode;

    /**
     * 运力返回的错误信息
     */
    private String delivery_resultmsg;
}
