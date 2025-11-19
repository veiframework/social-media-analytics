package com.chargehub.gateway.handler;

import com.chargehub.common.core.exception.CaptchaException;
import com.chargehub.common.core.web.domain.AjaxResult;

import com.chargehub.common.redis.service.ValidateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * 验证码获取
 *
 * @author ruoyi
 */
@Component
public class ValidateCodeHandler implements HandlerFunction<ServerResponse>
{
    @Autowired
    private ValidateCodeService validateCodeService;

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest)
    {
        AjaxResult ajax;
        try
        {
            ajax = validateCodeService.createCaptcha();
        }
        catch (CaptchaException | IOException e)
        {
            return Mono.error(e);
        }

        return ServerResponse.status(HttpStatus.OK)
                .headers(h -> {
                    h.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,"true");
                    h.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                    h.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
                    h.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                })
                .body(BodyInserters.fromValue(ajax));
    }
}
