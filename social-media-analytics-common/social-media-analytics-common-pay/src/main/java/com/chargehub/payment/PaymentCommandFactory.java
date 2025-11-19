package com.chargehub.payment;

import org.springframework.beans.factory.FactoryBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2025/04/01 10:18
 */
public class PaymentCommandFactory implements FactoryBean<PaymentCommand> {

    private final List<PaymentCommandHandler> paymentCommandHandlers;

    private final List<PaymentCommandDecorator> paymentCommandDecorators;

    public PaymentCommandFactory(List<PaymentCommandHandler> paymentCommandHandlers,
                                 List<PaymentCommandDecorator> paymentCommandDecorators) {
        this.paymentCommandHandlers = paymentCommandHandlers;
        this.paymentCommandDecorators = paymentCommandDecorators;
    }


    @Override
    public PaymentCommand getObject() {
        Map<PaymentChannel, PaymentCommandHandler> paymentCommandHandlerMap = paymentCommandHandlers.stream().collect(Collectors.toMap(PaymentCommandHandler::channel, v -> v));
        Map<String, PaymentCommandDecorator> paymentCommandDecoratorMap = paymentCommandDecorators.stream().collect(Collectors.toMap(PaymentCommandDecorator::businessTypeCode, v -> v));
        return new PaymentCommand(paymentCommandHandlerMap, paymentCommandDecoratorMap);
    }

    @Override
    public Class<?> getObjectType() {
        return PaymentCommand.class;
    }
}
