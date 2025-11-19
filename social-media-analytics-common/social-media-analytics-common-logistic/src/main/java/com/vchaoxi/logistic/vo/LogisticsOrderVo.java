package com.vchaoxi.logistic.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-31
 */
@Data
public class LogisticsOrderVo implements Serializable {


    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 商家id
     */
    private Integer shopId;

    /**
     * 用户订单id
     */
    private Integer orderId;

    /**
     * 物流订单类型  1揽件订单  2配送订单
     */
    private Integer orderType;

    /**
     * 我方订单单号  对应微信orderId
     */
    private String orderNo;

    /**
     * 运单号
     */
    private String waybillId;

    /**
     * 用户openid，当add_source=2时无需填写（不发送物流服务通知）
     */
    private String openid;

    /**
     * 快递公司ID
     */
    private String deliveryId;

    /**
     * 快递公司名称
     */
    private String deliveryName;

    /**
     * 快递公司客户编码
     */
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
     * 订单来源，0为小程序订单，2为App或H5订单，填2则不发送物流服务通知
     */
    private Integer addSource;

    /**
     * App或H5的appid，add_source=2时必填，需和开通了物流助手的小程序绑定同一open账号
     */
    private String wxAppid;

    /**
     * 发件人姓名，不超过64字节
     */
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
    private String senderProvince;

    private Integer senderProvinceCode;

    /**
     * 发件人市/地区，比如："广州市"，不超过64字节
     */
    private String senderCity;

    private Integer senderCityCode;

    /**
     * 发件人区/县，比如："海珠区"，不超过64字节
     */
    private String senderArea;

    private Integer senderAreaCode;

    /**
     * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
     */
    private String senderAddress;

    /**
     * 发件人姓名，不超过64字节
     */
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
    private String receiverProvince;

    private Integer receiverProvinceCode;

    /**
     * 发件人市/地区，比如："广州市"，不超过64字节
     */
    private String receiverCity;

    private Integer receiverCityCode;

    /**
     * 发件人区/县，比如："海珠区"，不超过64字节
     */
    private String receiverArea;

    private Integer receiverAreaCode;

    /**
     * 发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节
     */
    private String receiverAddress;

    /**
     * 包裹数量, 默认为1
     */
    private Integer cargoCount;

    /**
     * 货物总重量，比如1.2，单位是千克(kg)
     */
    private BigDecimal cargoWeight;

    /**
     * 货物长度，比如20.0，单位是厘米(cm)
     */
    private BigDecimal cargoSpaceX;

    /**
     * 货物宽度，比如15.0，单位是厘米(cm)
     */
    private BigDecimal cargoSpaceY;

    /**
     * 货物高度，比如10.0，单位是厘米(cm)
     */
    private BigDecimal cargoSpaceZ;

    /**
     *物品信息
     */
    private Object cargoDetailList;

    /**
     * 商家小程序的路径，建议为订单页面
     */
    private String shopWxaPath;

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

    /**
     * 商品详情列表，适配多商品场景，用以消息落地页展
     */
    private Object shopDetailList;

    /**
     * 是否保价，0 表示不保价，1 表示保价
     */
    private Integer useInsured;

    /**
     * 保价金额，单位是分，比如: 10000 表示 100 元
     */
    private Integer insuredValue;

    /**
     * 服务类型ID
     */
    private Integer serviceType;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * Unix 时间戳, 单位秒，顺丰必须传。 预期的上门揽件时间，0表示已事先约定取件时间；否则请传预期揽件时间戳，需大于当前时间，收件员会在预期时间附近上门。例如expect_time为“1557989929”，表示希望收件员将在2019年05月16日14:58:49-15:58:49内上门取货。说明：若选择 了预期揽件时间，请不要自己打单，由上门揽件的时候打印。如果是下顺丰散单，则必传此字段，否则不会有收件员上门揽件。
     */
    private Integer expectTime;

    /**
     * 分单策略，【0：线下网点签约，1：总部签约结算】，不传默认线下网点签约。目前支持圆通。
     */
    private Integer takeMode;

    /**
     * 订单状态  1正常  0取消
     */
    private Integer status;

    /**
     * 添加时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;




    /**
     * 商家名称
     */
    private String shopName;

    /**
     * 站点名称
     */
    private String lockerName;

    /**
     * 站点编号
     */
    private String lockerNo;

    /**
     * 站点类型
     */
    private Integer siteType;
}
