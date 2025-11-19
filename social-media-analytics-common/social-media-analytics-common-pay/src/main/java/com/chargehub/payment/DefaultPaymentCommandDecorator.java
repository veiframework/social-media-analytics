package com.chargehub.payment;

 
import com.chargehub.payment.bean.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 请注意控制数据库事务
 *
 * @author Zhanghaowei
 * @date 2025/05/09 11:53
 */
@Slf4j
public class DefaultPaymentCommandDecorator implements PaymentCommandDecorator {

    @Override
    public String createRefundNo(PaymentRefundParam param) {
        return "refund_" + PaymentConstant.uniqueId();
    }

    @Override
    public String businessTypeCode() {
        return "default";
    }

    @Override
    public void paymentNotify(PaymentNotifyParam param) {
        log.info(" [支付回调完成参数]: {}", param);
    }

    @Override
    public void refundNotify(PaymentRefundNotifyParam param) {
        log.info(" [退款回调完成-更新退款记录]: {}", param);
        //需要注意并发修改退款状态的问题
    }

    @Override
    public void createOrder(PaymentResult paymentResult) {
        log.info(" [创建订单结果]: {}", paymentResult);
    }

    @Override
    public void createRefund(PaymentRefundParam param) {
        log.info(" [创建退款记录-发起退款]: {}", param);

    }

    @Override
    public void afterCreateRefund(PaymentRefundResult result) {
        log.info(" [更新退款记录-发起退款结果-{}]: 退款单号-{}, 订单编号-{}", result.getRefundResult(), result.getOutRefundNo(), result.getOutTradeNo());
        //需要注意并发修改退款状态的问题
    }

}
