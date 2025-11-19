package com.chargehub.payment.wechat;

import com.chargehub.payment.bean.PaymentResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/04/02 17:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class WechatPaymentResult extends PaymentResult implements Serializable {
    private static final long serialVersionUID = 4573554825445770171L;

    /**
     * 收款的小程序appid或者公众号appid
     */
    private String appId;

    /**
     * 签名
     */
    private String sign;

    /**
     * 预支付交易会话标识
     */
    private String prepayId;

    /**
     *
     */
    private String partnerId;

    /**
     * 包名
     */
    @JsonProperty("package")
    private String packageValue;

    /**
     * 时间戳
     */
    private String timeStamp;

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 支付签名
     */
    private String paySign;

    /**
     * 调起支付UEL
     */
    private String mwebUrl;

    /**
     * 二维码链接
     */
    private String codeUrl;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     *
     */
    private String signature;



}
