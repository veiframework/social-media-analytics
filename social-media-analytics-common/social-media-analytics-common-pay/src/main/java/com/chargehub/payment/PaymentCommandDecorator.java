package com.chargehub.payment;
 
import com.chargehub.payment.bean.*;
import org.springframework.lang.Nullable;

/**
 * 请注意控制数据库事务
 *
 * @author Zhanghaowei
 * @date 2025/05/09 11:48
 */
public interface PaymentCommandDecorator {

    /**
     * 业务类型
     *
     * @return
     */
    String businessTypeCode();

    /**
     * 订单编号生成策略
     *
     * @param param
     * @return
     */
    default String createOutTradeNo(PaymentParam param) {
        return PaymentConstant.uniqueId();
    }

    /**
     * 退款单号生成策略
     *
     * @param param
     * @return
     */
    default String createRefundNo(PaymentRefundParam param) {
        return PaymentConstant.uniqueId();
    }


    /**
     * 支付回调
     * 请注意控制数据库事务
     *
     * @param param
     */
    void paymentNotify(PaymentNotifyParam param);

    /**
     * 退款回调
     * 请注意控制数据库事务
     * 需要注意并发修改退款状态的问题
     *
     * @param param
     */
    void refundNotify(PaymentRefundNotifyParam param);

    /**
     * 创建订单
     * 请注意控制数据库事务
     *
     * @param paymentResult
     */
    void createOrder(PaymentResult paymentResult);

    /**
     * 创建退款订单
     * 请注意控制数据库事务
     *
     * @param param
     */
    void createRefund(@Nullable PaymentRefundParam param);

    /**
     * 创建退款订单后
     * 请注意控制数据库事务
     * 需要注意并发修改退款状态的问题
     *
     * @param result
     */
    void afterCreateRefund(PaymentRefundResult result);
}
