package com.chargehub.thirdparty.api.domain.vo.logistics;

import lombok.Data;

import java.util.List;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/1/30 13:49
 * @Project：vc-shoes-server
 * @Package：com.vchaoxi.wx.open.vo.logistics
 * @Filename：LoAccountBindVo
 */
@Data
public class LoOrderAddVo {

    private Integer errcode;
    private String errmsg;

    /**
     * 订单ID，下单成功时返回
     */
    private String order_id;

    /**
     * 运单ID，下单成功时返回
     */
    private String waybill_id;

    /**
     * 快递侧错误码，下单失败时返回
     */
    private Integer delivery_resultcode;

    /**
     * 快递侧错误信息，下单失败时返回
     */
    private String delivery_resultmsg;

    /**
     * 运单信息
     */
    private List<Waybill> waybill_data;


    @Data
    public static class Waybill {

        /**
         * 运单信息 key
         */
        private String key;

        /**
         * 运单信息 value
         */
        private String value;
    }


}
