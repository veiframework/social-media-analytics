package com.vchaoxi.config;

import com.chargehub.payment.PaymentConfigManager;
import com.chargehub.payment.wechat.WechatPaymentCommandHandler;
import com.vchaoxi.payment.MallMemberPaymentHandler;
import com.vchaoxi.payment.MallPaymentHandler;
import com.vchaoxi.task.MallMemberTask;
import com.vchaoxi.task.MallOrderTask;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Zhanghaowei
 * @date 2025/08/11 18:14
 */
@Slf4j
@ComponentScan({"com.vchaoxi"})
@MapperScan({"com.vchaoxi.mapper"})
@EnableScheduling
public class MallConfiguration {


    @ConditionalOnProperty(prefix = "mall.order.task", value = "enabled", havingValue = "true")
    @Bean
    public MallOrderTask mallOrderTask() {
        log.info("enable mall order task");
        return new MallOrderTask();
    }

    @ConditionalOnProperty(prefix = "mall.member.task", value = "enabled", havingValue = "true")
    @Bean
    public MallMemberTask mallMemberTask() {
        log.info("enable mall member task");
        return new MallMemberTask();
    }

    @ConditionalOnProperty(prefix = "mall.order.payment-handler", value = "enabled", havingValue = "true")
    @Bean
    public MallPaymentHandler mallPaymentHandler() {
        log.info("enable mall order payment handler");
        return new MallPaymentHandler();
    }

    @ConditionalOnProperty(prefix = "mall.member.payment-handler", value = "enabled", havingValue = "true")
    @Bean
    public MallMemberPaymentHandler mallMemberPaymentHandler() {
        log.info("enable mall member payment handler");
        return new MallMemberPaymentHandler();
    }

}
