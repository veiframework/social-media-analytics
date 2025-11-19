package com.chargehub.payment.wechat;


import com.chargehub.payment.bean.PaymentParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/04/02 17:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class WechatPaymentParam extends PaymentParam implements Serializable {
    private static final long serialVersionUID = 3465625224649820686L;

    /**
     * 支付金额  单位元 【必传】
     */
    @NotBlank
    @Digits(integer = 10, fraction = 2)
    private String totalFee;

    /**
     * 付款方式
     * 微信支付：
     *  NATIVE
     *  JSAPI
     *  MWEB
     *
     * 支付宝支付：
     *  JSAPI_PAY
     *
     * 通联支付：
     * W01	微信扫码支付
     * W02	微信JS支付
     * W03	微信APP支付
     * W06	微信小程序支付
     * W11	微信订单支付
     * A01	支付宝扫码支付
     * A02	支付宝JS支付
     * A03	支付宝APP支付
     * U01	银联扫码支付(CSB)
     * U02	银联JS支付
     * S01	数币扫码支付
     * S03	数字货币H5
     * N03	网联支付
     */
    private String tradeType;

    /**
     * 付款用户openid
     * 微信支付：用户的小程序openid 或者 公众号openid  当tradeType为JSAPI时必传
     * 支付宝支付：buyer_id：买家支付宝用户ID   或者  buyer_open_id：买家支付宝用户唯一标识
     */
    private String openid;

    /**
     * 订单标题    【必传】
     */
    @NotBlank
    private String body;



    /**
     * 请求用户的ip地址    【必传】
     */
    private String spbillCreateIp;


    /**
     * 订单有效时长  单位分钟     【非必传】
     */
    private Integer timeExpire;



    /**
     * 商品id
     * 当 payType = 1 && tradeType = NATIVE  时必传
     */
    private String productId;

    /**
     * 未启用   附加参数
     */
    private String attach;


}
