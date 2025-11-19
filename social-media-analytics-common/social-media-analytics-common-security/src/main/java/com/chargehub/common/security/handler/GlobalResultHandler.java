package com.chargehub.common.security.handler;

import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.security.annotation.UnifyResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author Zhanghaowei
 * @date 2024/04/07 14:06
 */
@RestControllerAdvice
public class GlobalResultHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> containingClass = returnType.getContainingClass();
        return containingClass.isAnnotationPresent(UnifyResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof AjaxResult) {
            return body;
        } else if (body == null) {
            AjaxResult success = AjaxResult.success();
            success.put(AjaxResult.DATA_TAG, null);
            return success;
        } else {
            return AjaxResult.success(body);
        }
    }
}
