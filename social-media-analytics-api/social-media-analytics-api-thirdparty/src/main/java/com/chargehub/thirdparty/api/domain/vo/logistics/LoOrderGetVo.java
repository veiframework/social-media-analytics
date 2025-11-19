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
public class LoOrderGetVo {

    private Integer errcode;
    private String errmsg;

    /**
     * 运单 html 的 BASE64 结果
     */
    private String print_html;

    /**
     * 订单ID
     */
    private String order_id;

    /**
     * 快递公司ID
     */
    private String delivery_id;

    /**
     * 运单号
     */
    private String waybill_id;

    /**
     * 运单状态, 0正常，1取消
     */
    private Integer order_status;

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
