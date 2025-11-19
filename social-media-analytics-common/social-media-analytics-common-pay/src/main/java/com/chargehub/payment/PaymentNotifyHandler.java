package com.chargehub.payment;


import com.chargehub.payment.bean.PaymentNotifyParam;
import com.chargehub.payment.bean.PaymentRefundNotifyParam;

/**
 * @author Zhanghaowei
 * @date 2025/04/24 14:15
 */
public interface PaymentNotifyHandler {

    /**
     * 业务类型
     *
     * @return
     */
    String businessTypeCode();

    /**
     * 支付回调
     *
     * @param param
     */
    void paymentNotify(PaymentNotifyParam param);

    /**
     * 退款回调
     *
     * @param param
     */
    void refundNotify(PaymentRefundNotifyParam param);

}
