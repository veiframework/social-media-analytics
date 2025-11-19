package com.chargehub.gateway.config;

import com.chargehub.common.core.constant.Constants;
import com.chargehub.gateway.handler.SentinelFallbackHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;

/**
 * 网关限流配置
 *
 * @author ruoyi
 */
@Configuration
public class GatewayConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelFallbackHandler sentinelGatewayExceptionHandler() {
        return new SentinelFallbackHandler();
    }


    @Bean
    public UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Collections.singletonList(Constants.ASTERISK));
        corsConfig.setMaxAge(3600L);
        Constants.ALLOWED_METHOD.forEach(method -> corsConfig.addAllowedMethod(HttpMethod.valueOf(method)));
        Constants.ALLOWED_HEADERS.forEach(corsConfig::addAllowedHeader);
        Constants.EXPOSED_HEADERS.forEach(corsConfig::addExposedHeader);
        corsConfig.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(Constants.ALL_PATH, corsConfig);
        return source;
    }

    @Bean
    public CorsConfig corsConfig(UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource) {
        return new CorsConfig(urlBasedCorsConfigurationSource);
    }

}