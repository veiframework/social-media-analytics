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
public class LoOrderGetParam {


    /**
     * 订单 ID，需保证全局唯一
     */
    private String order_id;

    /**
     * 该参数仅在getOrder接口生效，batchGetOrder接口不生效。用户openid，当add_source=2时无需填写（不发送物流服务通知）
     */
    private String openid;

    /**
     * 快递公司ID，参见getAllDelivery, 必须和waybill_id对应
     */
    private String delivery_id;

    /**
     * 运单ID
     */
    private String waybill_id;

    /**
     * 该参数仅在getOrder接口生效，batchGetOrder接口不生效。获取打印面单类型【1：一联单，0：二联单】，默认获取二联单
     */
    private Integer print_type;

    /**
     * custom_remark
     */
    private String custom_remark;
}
