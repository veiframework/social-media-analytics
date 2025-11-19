package com.chargehub.payment;
 

import com.chargehub.payment.bean.*;

import java.util.function.Consumer;


/**
 * @author Zhanghaowei
 * @date 2025/03/26 17:25
 */
public interface PaymentCommandHandler {

    /**
     * 创建订单
     *
     * @param paymentChannel
     * @param param
     * @return
     */
    PaymentResult createOrder(PaymentParam param, PaymentChannel paymentChannel);

    /**
     * 创建退款订单
     *
     * @param paymentChannel
     * @param param
     * @return
     */
    PaymentRefundResult createRefund(PaymentRefundParam param, PaymentChannel paymentChannel);


    /**
     * 支付回调
     *
     * @param consumer
     * @param paymentChannel
     * @param param
     * @return
     */
    String paymentNotify(PaymentNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentNotifyParam> consumer);

    /**
     * 退款回调
     *
     * @param consumer
     * @param paymentChannel
     * @param param
     * @return
     */
    String refundNotify(PaymentRefundNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentRefundNotifyParam> consumer);


    /**
     * 支付渠道
     *
     * @return
     */
    PaymentChannel channel();
}
