package com.chargehub.thirdparty.api.domain.dto.logistics;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/1/30 13:46
 * @Project：vc-shoes-server
 * @Package：com.vchaoxi.wx.open.param.logistics
 * @Filename：LoAccountBindParam
 */
@Data
public class LoOrderAddParam {


    /**
     * 订单ID，须保证全局唯一，不超过512字节
     */
    private String order_id;

    /**
     * 用户openid，当add_source=2时无需填写（不发送物流服务通知）
     */
    private String openid;

    /**
     * 快递公司ID，参见getAllDelivery
     */
    private String delivery_id;

    /**
     * 快递客户编码或者现付编码
     */
    private String biz_id;

    /**
     * 快递备注信息，比如"易碎物品"，不超过1024字节
     */
    private String custom_remark;

    /**
     * 订单标签id，用于平台型小程序区分平台上的入驻方，tagid须与入驻方账号一一对应，非平台型小程序无需填写该字段
     */
    private Integer tagid;

    /**
     * 订单来源，0为小程序订单，2为App或H5订单，填2则不发送物流服务通知
     */
    private Integer add_source;

    /**
     * App或H5的appid，add_source=2时必填，需和开通了物流助手的小程序绑定同一open账号
     */
    private String wx_appid;

    /**
     * 发件人信息
     */
    private Sender sender;

    /**
     * 收件人信息
     */
    private Receiver receiver;

    /**
     * 包裹信息，将传递给快递公司
     */
    private Cargo cargo;


    /**
     * 商品信息，会展示到物流服务通知和电子面单中
     */
    private Shop shop;

    /**
     * 保价信息
     */
    private Insured insured;

    /**
     * 服务类型
     */
    private Service service;


    /**
     * Unix 时间戳, 单位秒，顺丰必须传。 预期的上门揽件时间，0表示已事先约定取件时间；
     * 否则请传预期揽件时间戳，需大于当前时间，收件员会在预期时间附近上门。
     * 例如expect_time为“1557989929”，表示希望收件员将在2019年05月16日14:58:49-15:58:49内上门取货。
     * 说明：若选择 了预期揽件时间，请不要自己打单，由上门揽件的时候打印。如果是下顺丰散单，则必传此字段，否则不会有收件员上门揽件。
     */
    private Integer expect_time;

    /**
     * 分单策略，【0：线下网点签约，1：总部签约结算】，不传默认线下网点签约。目前支持圆通。
     */
    private Integer take_mode;









    /**
     * 发件人信息
     */
    @Data
    public static class Sender {

        /**
         * 发件人姓名，不超过64字节
         */
        private String name;

        /**
         * 发件人座机号码，若不填写则必须填写 mobile，不超过32字节
         */
        private String tel;

        /**
         * 发件人手机号码，若不填写则必须填写 tel，不超过32字节
         */
        private String mobile;

        /**
         * 发件人公司名称，不超过64字节
         */
        private String company;

        /**
         * 发件人邮编，不超过10字节
         */
        private String post_code;

        /**
         *发件人国家，不超过64字节
         */
        private String country;

        /**
         * 发件人省份，比如："广东省"，不超过64字节
         */
        private String province;

        /**
         * 发件人市/地区，比如："广州市"，不超过64字节
         */
        private String city;

        /**
         * 发件人区/县，比如："海珠区"，不超过64字节
         */
        private String area;

        /**
         * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
         */
        private String address;
    }


    /**
     * 收件人信息
     */
    @Data
    public static class Receiver {

        /**
         * 发件人姓名，不超过64字节
         */
        private String name;

        /**
         * 发件人座机号码，若不填写则必须填写 mobile，不超过32字节
         */
        private String tel;

        /**
         * 发件人手机号码，若不填写则必须填写 tel，不超过32字节
         */
        private String mobile;

        /**
         * 发件人公司名称，不超过64字节
         */
        private String company;

        /**
         * 发件人邮编，不超过10字节
         */
        private String post_code;

        /**
         * 发件人国家，不超过64字节
         */
        private String country;

        /**
         * 发件人省份，比如："广东省"，不超过64字节
         */
        private String province;

        /**
         * 发件人市/地区，比如："广州市"，不超过64字节
         */
        private String city;

        /**
         * 发件人区/县，比如："海珠区"，不超过64字节
         */
        private String area;

        /**
         * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
         */
        private String address;
    }


    /**
     * 商品信息，会展示到物流服务通知和电子面单中
     */
    @Data
    public static class Cargo {

        /**
         * 包裹数量, 默认为1
         */
        private Integer count;

        /**
         *货物总重量，比如1.2，单位是千克(kg)
         */
        private BigDecimal weight;

        /**
         * 货物长度，比如20.0，单位是厘米(cm)
         */
        private BigDecimal space_x;

        /**
         * 货物宽度，比如15.0，单位是厘米(cm)
         */
        private BigDecimal space_y;

        /**
         * 货物高度，比如10.0，单位是厘米(cm)
         */
        private BigDecimal space_z;

        /**
         * 商品信息
         */
        private List<CargoDetail> detail_list;


        /**
         * 商品信息
         */
        @Data
        public static class CargoDetail {

            /**
             * 商品名称
             */
            private String name;

            /**
             * 商品数量
             */
            private String count;
        }
    }




    /**
     * 商品信息，会展示到物流服务通知和电子面单中
     */
    @Data
    public static class Shop {

        /**
         * 商家小程序的路径，建议为订单页面
         */
        private String wxa_path;

        /**
         * 商品缩略图 url；shop.detail_list为空则必传，shop.detail_list非空可不传
         */
        private String img_url;

        /**
         * 商品名称, 不超过128字节；shop.detail_list为空则必传，shop.detail_list非空可不传
         */
        private String goods_name;

        /**
         * 商品数量；shop.detail_list为空则必传。shop.detail_list非空可不传，默认取shop.detail_list的size
         */
        private Integer goods_count;


        /**
         * 商品详情列表，适配多商品场景，用以消息落地页展示。（新规范，新接入商家建议用此字段）
         */
        private List<ShopDetail> detail_list;


        /**
         * 商品信息
         */
        @Data
        public static class ShopDetail {

            /**
             * 商品缩略图 url；shop.detail_list为空则必传，shop.detail_list非空可不传
             */
            private String goods_img_url;

            /**
             * 商品名称, 不超过128字节；shop.detail_list为空则必传，shop.detail_list非空可不传
             */
            private String goods_name;

            /**
             * 商品数量；shop.detail_list为空则必传。shop.detail_list非空可不传，默认取shop.detail_list的size
             */
            private Integer goods_count;
        }
    }



    /**
     * 保价信息
     */
    @Data
    public static class Insured {

        /**
         * 是否保价，0 表示不保价，1 表示保价
         */
        private Integer use_insured;

        /**
         * 保价金额，单位是分，比如: 10000 表示 100 元
         */
        private Integer insured_value;
    }


    /**
     * 服务类型
     */
    @Data
    public static class Service {

        /**
         * 服务类型ID，详见已经支持的快递公司基本信息
         */
        private Integer service_type;

        /**
         * 服务名称，详见已经支持的快递公司基本信息
         */
        private String service_name;
    }



}
