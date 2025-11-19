package com.vchaoxi.logistic.param;

import com.chargehub.common.core.utils.validation.customized.EnumValue;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-31
 */
@Data
public class LogisticsOrderParam {


    @NotNull(groups = {Cancel.class})
    private Integer id;

    /**
     * 用户订单id
     */
    @NotNull(groups = {Create.class})
    private Integer orderId;

    /**
     * 物流订单类型  1揽件订单  2配送订单
     */
    @EnumValue(intValues = {1,2},groups = {Create.class})
    private Integer orderType;

    /**
     * 快递公司ID
     */
    @NotEmpty(groups = {Create.class})
    private String deliveryId;

    /**
     * 快递公司名称
     */
    @NotEmpty(groups = {Create.class})
    private String deliveryName;

    /**
     * 快递公司客户编码
     */
    @NotEmpty(groups = {Create.class})
    private String bizId;

    /**
     * 快递备注信息，比如"易碎物品"，不超过1024字节
     */
    private String customRemark;

    /**
     * 订单标签id，用于平台型小程序区分平台上的入驻方，tagid须与入驻方账号一一对应，非平台型小程序无需填写该字段
     */
    private Integer tagid;

    /**
     * 发件人姓名，不超过64字节
     */
    @NotEmpty(groups = {Create.class})
    private String senderName;

    /**
     * 发件人座机号码，若不填写则必须填写 mobile，不超过32字节
     */
    private String senderTel;

    /**
     * 发件人手机号码，若不填写则必须填写 tel，不超过32字节
     */
    private String senderMobile;

    /**
     * 发件人公司名称，不超过64字节
     */
    private String senderCompany;

    /**
     * 发件人邮编，不超过10字节
     */
    private String senderPostCode;

    /**
     * 发件人国家，不超过64字节
     */
    private String senderCountry;

    /**
     * 发件人省份，比如："广东省"，不超过64字节
     */
    @NotEmpty(groups = {Create.class})
    private String senderProvince;

    @NotNull(groups = {Create.class})
    private Integer senderProvinceCode;

    /**
     * 发件人市/地区，比如："广州市"，不超过64字节
     */
    @NotEmpty(groups = {Create.class})
    private String senderCity;

    @NotNull(groups = {Create.class})
    private Integer senderCityCode;

    /**
     * 发件人区/县，比如："海珠区"，不超过64字节
     */
    @NotEmpty(groups = {Create.class})
    private String senderArea;

    @NotNull(groups = {Create.class})
    private Integer senderAreaCode;

    /**
     * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
     */
    @NotEmpty(groups = {Create.class})
    private String senderAddress;

    /**
     * 发件人姓名，不超过64字节
     */
    @NotEmpty(groups = {Create.class})
    private String receiverName;

    /**
     * 发件人座机号码，若不填写则必须填写 mobile，不超过32字节
     */
    private String receiverTel;

    /**
     * 发件人手机号码，若不填写则必须填写 tel，不超过32字节
     */
    private String receiverMobile;

    /**
     * 发件人公司名称，不超过64字节
     */
    private String receiverCompany;

    /**
     * 发件人邮编，不超过10字节
     */
    private String receiverPostCode;

    /**
     * 发件人国家，不超过64字节
     */
    private String receiverCountry;

    /**
     * 发件人省份，比如："广东省"，不超过64字节
     */
    @NotEmpty(groups = {Create.class})
    private String receiverProvince;

    @NotNull(groups = {Create.class})
    private Integer receiverProvinceCode;

    /**
     * 发件人市/地区，比如："广州市"，不超过64字节
     */
    @NotEmpty(groups = {Create.class})
    private String receiverCity;

    @NotNull(groups = {Create.class})
    private Integer receiverCityCode;

    /**
     * 发件人区/县，比如："海珠区"，不超过64字节
     */
    @NotEmpty(groups = {Create.class})
    private String receiverArea;

    @NotNull(groups = {Create.class})
    private Integer receiverAreaCode;

    /**
     * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
     */
    @NotEmpty(groups = {Create.class})
    private String receiverAddress;

    /**
     * 包裹数量, 默认为1
     */
    @NotNull(groups = {Create.class})
    @Min(value = 1,groups = {Create.class})
    private Integer cargoCount;

    /**
     * 货物总重量，比如1.2，单位是千克(kg)
     */
    @NotNull(groups = {Create.class})
    @Min(value = 0,groups = {Create.class})
    private BigDecimal cargoWeight;

    /**
     * 货物长度，比如20.0，单位是厘米(cm)
     */
    @NotNull(groups = {Create.class})
    @Min(value = 0,groups = {Create.class})
    private BigDecimal cargoSpaceX;

    /**
     * 货物宽度，比如15.0，单位是厘米(cm)
     */
    @NotNull(groups = {Create.class})
    @Min(value = 0,groups = {Create.class})
    private BigDecimal cargoSpaceY;

    /**
     * 货物高度，比如10.0，单位是厘米(cm)
     */
    @NotNull(groups = {Create.class})
    @Min(value = 0,groups = {Create.class})
    private BigDecimal cargoSpaceZ;

    /**
     * 商品详情列表，适配多商品场景，用以消息落地页展
     */
    private List<ShopDetail> shopDetailList;

    /**
     * 商品信息
     */
    private List<CargoDetail> cargoDetailList;

    /**
     * 是否保价，0 表示不保价，1 表示保价
     */
    @EnumValue(intValues = {0,1},groups = {Create.class})
    private Integer useInsured;

    /**
     * 保价金额，单位是分，比如: 10000 表示 100 元
     */
    private Integer insuredValue;

    /**
     * 服务类型ID
     */
    @NotNull(groups = {Create.class})
    private Integer serviceType;

    /**
     * 服务名称
     */
    @NotEmpty(groups = {Create.class})
    private String serviceName;

    /**
     * Unix 时间戳, 单位秒，顺丰必须传。 预期的上门揽件时间，0表示已事先约定取件时间；
     * 否则请传预期揽件时间戳，需大于当前时间，收件员会在预期时间附近上门。
     * 例如expect_time为“1557989929”，表示希望收件员将在2019年05月16日14:58:49-15:58:49内上门取货。
     * 说明：若选择 了预期揽件时间，请不要自己打单，由上门揽件的时候打印。如果是下顺丰散单，则必传此字段，否则不会有收件员上门揽件。
     */
    private Integer expectTime;

    /**
     * 分单策略，【0：线下网点签约，1：总部签约结算】，不传默认线下网点签约。目前支持圆通。
     */
    @EnumValue(intValues = {0,1},groups = {Create.class})
    private Integer takeMode;


    /**
     * 商品信息
     */
    @Data
    public static class ShopDetail {
        /**
         * 商品缩略图 url；shop.detail_list为空则必传，shop.detail_list非空可不传。
         */
        private String shopImgUrl;

        /**
         * 商品名称, 不超过128字节；shop.detail_list为空则必传，shop.detail_list非空可不传。
         */
        private String shopGoodsName;

        /**
         * 商品数量；shop.detail_list为空则必传。shop.detail_list非空可不传，默认取shop.detail_list的size
         */
        private Integer shopGoodsCount;
    }


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










    public interface Create {}
    public interface Cancel {}
}
