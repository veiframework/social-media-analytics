package com.vchaoxi.vo;

import lombok.Data;

@Data
public class WxDeliverUploadShippingResultVo {
    /**
     * 错误码 0 代表成功
     */
    private Integer errcode;

    /**
     * 错误原因
     */
    private String errmsg;
}
