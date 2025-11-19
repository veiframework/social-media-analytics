package com.chargehub.payment.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/04/01 09:49
 */
@Data
public class PaymentRefundResult implements Serializable {
    private static final long serialVersionUID = -1791226477119393496L;


    /**
     * 退款结果
     * true : 成功
     * false : 失败
     */
    private Boolean refundResult;

    /**
     * 失败描述
     */
    private String message;


    /**
     * 退款单号
     */
    private String outRefundNo;

    /**
     * 订单总金额
     */
    private Integer totalFee;

    /**
     * 本次退款金额
     */
    private Integer refundFee;


    /**
     * 支付平台返回的交易流水号, 对方订单号(目前只有通联支付用到了本参数)
     */
    private String transactionId;

    private String outTradeNo;

    public PaymentRefundResult() {
    }

}
