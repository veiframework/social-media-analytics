package com.chargehub.payment.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/05/08 11:45
 */
@Data
public class BasePaymentProperties implements Serializable {
    private static final long serialVersionUID = 2640866178774475058L;

    private boolean enabled = true;


    private String id = "default";

    /**
     * 是否代理支付, 如果是则使用子商户支付, 否则由服务商平台定期清分并支付给指定账户
     */
    private boolean proxyPayment;

    /**
     * 过期时间单位秒
     */
    private Integer expireSeconds = 300;

    /**
     * 支付回调地址
     */
    private String notifyUrl = "https://sit-charge-api.lychxny.com/payment-api/payment/notify";

    /**
     * 退款回调通知地址
     */
    private String refundNotifyUrl = "https://sit-charge-api.lychxny.com/payment-api/payment/refund/notify";


    public String buildNotifyUrl(String url, String channel, String id, String businessTypeCode) {
        return String.join("/", url, channel, "config", id, "business", businessTypeCode);
    }


}
