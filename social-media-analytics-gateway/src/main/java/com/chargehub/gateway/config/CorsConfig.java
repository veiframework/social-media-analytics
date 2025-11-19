package com.chargehub.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsProcessor;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.DefaultCorsProcessor;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author Zhanghaowei
 * @date 2024/04/19 10:43
 */
public class CorsConfig implements WebFilter {

    private final CorsConfigurationSource configSource;

    private final CorsProcessor processor;

    public CorsConfig(CorsConfigurationSource configSource) {
        this(configSource, new DefaultCorsProcessor());
    }

    public CorsConfig(CorsConfigurationSource configSource, CorsProcessor processor) {
        Assert.notNull(configSource, "CorsConfigurationSource must not be null");
        Assert.notNull(processor, "CorsProcessor must not be null");
        this.configSource = configSource;
        this.processor = processor;
    }

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(exchange);
        if (CorsUtils.isPreFlightRequest(request)) {
            this.processor.process(corsConfiguration, exchange);
            return Mono.empty();
        }
        Mono<Void> mono = chain.filter(exchange);
        ServerHttpResponse response = exchange.getResponse();
        if (CorsUtils.isCorsRequest(request) && !response.isCommitted()) {
            mono.then(Mono.just(exchange)).map(serverWebExchange -> {
                this.processor.process(corsConfiguration, serverWebExchange);
                return serverWebExchange;
            }).then();
        }
        return mono;
    }
}
