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
public class LoDeliveryGetallVo {

    private Integer count;

    /**
     * 快递公司信息列表
     */
    private List<Delivery> data;

    @Data
    public static class Delivery {

        /**
         * 快递公司 ID
         */
        private String delivery_id;

        /**
         * 快递公司名称
         */
        private String delivery_name;

        /**
         * 是否支持散单, 1表示支持
         */
        private Integer can_use_cash;

        /**
         * 是否支持查询面单余额, 1表示支持
         */
        private Integer can_get_quota;

        /**
         * 支持的服务类型
         */
        private List<serviceType> service_type;

        /**
         * 散单对应的bizid，当can_use_cash=1时有效
         */
        private String cash_biz_id;



        @Data
        public static class serviceType {

            /**
             * service_type
             */
            private Integer service_type;

            /**
             * 服务类型名称
             */
            private String service_name;
        }
    }
}
