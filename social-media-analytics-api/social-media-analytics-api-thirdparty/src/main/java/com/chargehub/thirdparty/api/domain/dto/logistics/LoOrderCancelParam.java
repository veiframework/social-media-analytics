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
public class LoOrderCancelParam {


    /**
     * 用户openid，当add_source=2时无需填写（不发送物流服务通知）
     */
    private String openid;

    /**
     * 快递公司ID，参见getAllDelivery
     */
    private String delivery_id;

    /**
     * 运单ID
     */
    private String waybill_id;

    /**
     * ID，需保证全局唯一
     */
    private String order_id;
}
