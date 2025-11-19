package com.chargehub.gateway.filter;

import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.utils.EncryptUtil;
import com.chargehub.common.core.utils.ServletUtils;
import com.chargehub.gateway.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author：xiaobaopiqi
 * @Date：2023/7/31 11:47
 * @Project：express-ark-server
 * @Package：com.youlin.express.gateway.filter
 * @Filename：SignValidationFilter
 */
@Slf4j
@Component
public class ValidationSignFilter implements GlobalFilter, Ordered {





    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        log.info("【签名参数验证】请求路径：{}", path);
        // 判断当前请求是否需要进行签名校验
        if (needSignValidation(path)) {
            // 执行签名校验逻辑
            if (!validateSign(request)){
                return ServletUtils.webFluxResponseWriter(exchange.getResponse(), "请求资源不存在", HttpStatus.NOT_FOUND);
            }
        }
        return chain.filter(exchange);
    }


    /**
     * 判断是否需要进行签名校验
     * @param path
     * @return
     */
    private boolean needSignValidation(String path) {

        return GatewayConstant.VERIFY_SIGN_PATHS.contains(path) ? true : false;
    }


    /**
     * 执行参数校验逻辑
     * @param request
     * @return
     */
    private boolean validateSign(ServerHttpRequest request) {
        HttpHeaders httpHeaders = request.getHeaders();
        String sign = httpHeaders.getFirst(GatewayConstant.SIGN);
        String timeStamp = httpHeaders.getFirst(GatewayConstant.TIME_STAMP);

        if(StringUtils.isEmpty(sign) || StringUtils.isEmpty(timeStamp)) {
            log.info("【签名参数验证】签名参数为空............");
            return false;
        }

        //判断当前时间戳间隔是否超过5分钟
        Long currentTimeStamp = System.currentTimeMillis();
        if(Math.abs((currentTimeStamp / 1000) - Long.parseLong(timeStamp)) > 5 * 60) {
            log.info("【签名参数验证】请求时间戳超过5分钟............");
            return false;
        }

        //将时间戳添加盐值进行MD5加密
        String md5Sign = EncryptUtil.encrypt(timeStamp + GatewayConstant.SALT,"MD5");
        return md5Sign.equals(sign) ? true : false;
    }


    @Override
    public int getOrder() {
        // 指定过滤器的执行顺序，数值越小优先级越高
        return 0;
    }
}
