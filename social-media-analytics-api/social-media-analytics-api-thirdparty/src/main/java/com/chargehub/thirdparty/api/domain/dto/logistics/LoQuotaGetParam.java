package com.chargehub.thirdparty.api.domain.dto.logistics;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/1/30 13:46
 * @Project：vc-shoes-server
 * @Package：com.vchaoxi.wx.open.param.logistics
 * @Filename：LoAccountBindParam
 */
@Data
public class LoQuotaGetParam {


    /**
     * 快递公司ID，参见getAllDelivery
     */
    private String delivery_id;

    /**
     * 快递公司客户编码
     */
    private String biz_id;
}
