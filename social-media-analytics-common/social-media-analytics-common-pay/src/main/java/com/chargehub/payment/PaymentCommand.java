package com.chargehub.payment;

 
import com.chargehub.payment.bean.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Zhanghaowei
 * @date 2025/04/01 10:18
 */
@Slf4j
public class PaymentCommand implements PaymentCommandHandler {

    private final Map<PaymentChannel, PaymentCommandHandler> paymentCommandHandlerMap;

    private final Map<String, PaymentCommandDecorator> paymentCommandDecoratorMap;

    public PaymentCommand(Map<PaymentChannel, PaymentCommandHandler> paymentCommandHandlerMap,
                          Map<String, PaymentCommandDecorator> paymentCommandDecoratorMap) {
        this.paymentCommandHandlerMap = paymentCommandHandlerMap;
        this.paymentCommandDecoratorMap = paymentCommandDecoratorMap;
    }


    @Override
    public synchronized PaymentResult createOrder(PaymentParam param, PaymentChannel paymentChannel) {
        if (StringUtils.isBlank(param.getOutTradeNo())) {
            String outTradeNo = this.doGetPaymentCommandDecorator(param.getBusinessTypeCode()).createOutTradeNo(param);
            param.setOutTradeNo(outTradeNo);
        }
        PaymentCommandHandler paymentCommandHandler = this.doGetPaymentCommandHandler(paymentChannel);
        PaymentResult result = paymentCommandHandler.createOrder(param, paymentChannel);
        if (BooleanUtils.isTrue(result.getResult())) {
            this.doGetPaymentCommandDecorator(param.getBusinessTypeCode()).createOrder(result);
        }
        return result;
    }

    @Override
    public synchronized PaymentRefundResult createRefund(PaymentRefundParam param, PaymentChannel paymentChannel) {
        param.setRefundFee(new BigDecimal(param.getRefundFee()).movePointRight(NumberUtils.INTEGER_TWO).toString());
        param.setTotalFee(new BigDecimal(param.getTotalFee()).movePointRight(NumberUtils.INTEGER_TWO).toString());
        if (StringUtils.isBlank(param.getOutRefundNo())) {
            String refundNo = this.doGetPaymentCommandDecorator(param.getBusinessTypeCode()).createRefundNo(param);
            param.setOutRefundNo(refundNo);
        }
        PaymentCommandDecorator paymentCommandDecorator = this.doGetPaymentCommandDecorator(param.getBusinessTypeCode());
        paymentCommandDecorator.createRefund(param);
        PaymentRefundResult result = this.doGetPaymentCommandHandler(paymentChannel).createRefund(param, paymentChannel);
        paymentCommandDecorator.afterCreateRefund(result);
        return result;
    }


    @Override
    public synchronized String paymentNotify(PaymentNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentNotifyParam> consumer) {
        PaymentCommandHandler paymentCommandHandler = this.doGetPaymentCommandHandler(paymentChannel);
        return paymentCommandHandler.paymentNotify(param, paymentChannel, consumer);
    }

    @Override
    public synchronized String refundNotify(PaymentRefundNotifyParam param, PaymentChannel paymentChannel, Consumer<PaymentRefundNotifyParam> consumer) {
        PaymentCommandHandler paymentCommandHandler = this.doGetPaymentCommandHandler(paymentChannel);
        return paymentCommandHandler.refundNotify(param, paymentChannel, consumer);
    }


    public String paymentNotify(PaymentNotifyParam param, PaymentChannel paymentChannel) {
        return this.paymentNotify(param, paymentChannel, notifyParam -> this.doGetPaymentCommandDecorator(param.getBusinessTypeCode()).paymentNotify(notifyParam));
    }


    public String refundNotify(PaymentRefundNotifyParam param, PaymentChannel paymentChannel) {
        return this.refundNotify(param, paymentChannel, notifyParam -> this.doGetPaymentCommandDecorator(param.getBusinessTypeCode()).refundNotify(notifyParam));
    }

    public PaymentCommandHandler doGetPaymentCommandHandler(PaymentChannel paymentChannel) {
        PaymentCommandHandler paymentCommandHandler = this.paymentCommandHandlerMap.get(paymentChannel);
        Assert.notNull(paymentCommandHandler, "not support payment platform!");
        return paymentCommandHandler;
    }

    public PaymentCommandDecorator doGetPaymentCommandDecorator(String businessTypeCode) {
        PaymentCommandDecorator paymentCommandDecorator = this.paymentCommandDecoratorMap.get(businessTypeCode);
        if (paymentCommandDecorator == null) {
            log.warn(" [not found payment command decorator]: {}", businessTypeCode);
            return this.paymentCommandDecoratorMap.get("default");
        }
        return paymentCommandDecorator;
    }


    @Override
    public PaymentChannel channel() {
        return null;
    }

}
