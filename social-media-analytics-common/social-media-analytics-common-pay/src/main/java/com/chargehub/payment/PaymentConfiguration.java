package com.chargehub.payment;

import com.chargehub.payment.alipay.AliPaymentCommandHandler;
import com.chargehub.payment.allinpay.AllInPaymentCommandHandler;
import com.chargehub.payment.baidu.BaiduPaymentCommandHandler;
import com.chargehub.payment.wechat.WechatPaymentCommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2025/08/07 15:36
 */

@ComponentScan({"com.chargehub.z9.server"})
@MapperScan({"com.chargehub.z9.server.mapper"})
@ConditionalOnClass(VeiPaymentProperties.class)
@EnableConfigurationProperties(VeiPaymentProperties.class)
@Slf4j
public class PaymentConfiguration {

    @Bean
    public PaymentCommandFactory paymentCommandFactory(List<PaymentCommandHandler> paymentCommandHandlers,
                                                       List<PaymentCommandDecorator> paymentCommandDecorators) {
        return new PaymentCommandFactory(paymentCommandHandlers, paymentCommandDecorators);
    }

    @ConditionalOnMissingBean
    @Bean
    public PaymentConfigManager defaultPaymentConfigManager(VeiPaymentProperties veiPaymentProperties) {
        return new DefaultPaymentConfigManager(veiPaymentProperties);
    }

    @ConditionalOnProperty(prefix = "vei.payment.wechat", value = "enabled", havingValue = "true")
    @Bean
    public WechatPaymentCommandHandler wechatPaymentCommandHandler(PaymentConfigManager paymentConfigManager) {
        return new WechatPaymentCommandHandler(paymentConfigManager);
    }

    @ConditionalOnProperty(prefix = "vei.payment.alipay", value = "enabled", havingValue = "true")
    @Bean
    public AliPaymentCommandHandler aliPaymentCommandHandler(PaymentConfigManager paymentConfigManager) {
        return new AliPaymentCommandHandler(paymentConfigManager);
    }

    @ConditionalOnProperty(prefix = "vei.payment.baidu", value = "enabled", havingValue = "true")
    @Bean
    public BaiduPaymentCommandHandler baiduPaymentCommandHandler(PaymentConfigManager paymentConfigManager) {
        return new BaiduPaymentCommandHandler(paymentConfigManager);
    }

    @ConditionalOnProperty(prefix = "vei.payment.allinpay", value = "enabled", havingValue = "true")
    @Bean
    public AllInPaymentCommandHandler allInPaymentCommandHandler(PaymentConfigManager paymentConfigManager) {
        return new AllInPaymentCommandHandler(paymentConfigManager);
    }




}
