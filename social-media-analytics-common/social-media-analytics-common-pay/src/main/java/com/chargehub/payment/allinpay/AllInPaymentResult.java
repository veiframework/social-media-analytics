package com.chargehub.payment.allinpay;

import com.chargehub.payment.bean.PaymentResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/04/12 11:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AllInPaymentResult extends PaymentResult implements Serializable {
    private static final long serialVersionUID = -5179795221696900106L;

    /**
     * 收款的小程序appid或者公众号appid
     */
    private String appId;

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
     * 支付宝交易号
     */
    private String tradeNo;

}
