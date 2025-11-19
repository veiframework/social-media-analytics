package com.chargehub.common.security.handler;

import com.chargehub.common.core.exception.CaptchaException;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.redis.service.ValidateCodeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;

/**
 * @author Zhanghaowei
 * @date 2025/08/20 16:34
 */
public class WebValidateCodeHandler implements HandlerFunction<ServerResponse> {

    private final ValidateCodeService validateCodeService;

    public WebValidateCodeHandler(ValidateCodeService validateCodeService) {
        this.validateCodeService = validateCodeService;
    }


    @Override
    public ServerResponse handle(ServerRequest request) throws Exception {
        AjaxResult ajax;
        try
        {
            ajax = validateCodeService.createCaptcha();
        }
        catch (CaptchaException | IOException e)
        {
            ajax = AjaxResult.error(e.getMessage());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ajax);
        }

        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> {
                    h.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,"true");
                    h.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                    h.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
                    h.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                }).body(ajax);
    }
}
