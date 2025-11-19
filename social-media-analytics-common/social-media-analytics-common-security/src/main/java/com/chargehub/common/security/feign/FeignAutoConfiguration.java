package com.chargehub.common.security.feign;

import feign.Client;
import feign.RequestInterceptor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.net.URI;

/**
 * Feign 配置注册
 *
 * @author ruoyi
 **/
@Configuration
public class FeignAutoConfiguration {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignRequestInterceptor();
    }

    @Bean
    @ConditionalOnProperty(prefix = "hub", name = "standalone", havingValue = "true")
    public Client feignClient() {
        return new Client.Default(null, null);
    }

}
