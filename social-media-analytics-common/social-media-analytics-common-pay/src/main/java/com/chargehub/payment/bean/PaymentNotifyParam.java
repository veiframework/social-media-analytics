package com.chargehub.payment.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/04/23 18:08
 */
@Data
public class PaymentNotifyParam {

    /**
     * 支付配置id
     */
    private String paymentConfigId;

    /**
     * 业务类型标识
     */
    private String businessTypeCode;

    /**
     * 原始数据
     */
    private Object rawData;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 支付平台的交易流水号
     */
    private String transactionId;

    /**
     * 扩展参数
     */
    private Map<String, String> extendParams = new HashMap<>();

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 通知消息
     */
    private String message;


}
