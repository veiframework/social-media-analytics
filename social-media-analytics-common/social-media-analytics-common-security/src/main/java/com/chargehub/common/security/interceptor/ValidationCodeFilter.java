package com.chargehub.common.security.interceptor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.redis.service.ValidateCodeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/08/21 15:29
 */
@Slf4j
public class ValidationCodeFilter implements Filter {

    private static final AntPathRequestMatcher ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/auth-api/login", HttpMethod.POST.name());

    private final ValidateCodeService validateCodeService;

    private final ObjectMapper objectMapper;

    private static final String CODE = "code";

    private static final String UUID = "uuid";


    public ValidationCodeFilter(ValidateCodeService validateCodeService,
                                ObjectMapper objectMapper) {
        this.validateCodeService = validateCodeService;
        this.objectMapper = objectMapper;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        boolean matches = ANT_PATH_REQUEST_MATCHER.matches(httpServletRequest);
        if (!matches) {
            chain.doFilter(request, response);
            return;
        }
        try {
            RepeatableRequest repeatableRequest = new RepeatableRequest(httpServletRequest);
            Map<String, Object> requestBody = getRequestBody(repeatableRequest);
            validateCodeService.checkCaptcha((String) requestBody.getOrDefault(CODE, ""), (String) requestBody.getOrDefault(UUID, ""));
            chain.doFilter(repeatableRequest, response);
        } catch (Exception e) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setHeader("Expires", "0");
            httpServletResponse.setHeader("Pragma", "No-cache");
            httpServletResponse.setHeader("Cache-Control", "no-cache");

            try (OutputStream outputStream = httpServletResponse.getOutputStream()) {
                objectMapper.writeValue(outputStream, AjaxResult.error(e.getMessage()));
                outputStream.flush();
            }
        }

    }


    public Map<String, Object> getRequestBody(HttpServletRequest request) {
        try {
            TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {
            };
            return objectMapper.readValue(request.getInputStream(), mapTypeReference);
        } catch (Exception e) {
            log.error("error parsing body: {}", e.getMessage());
            return new HashMap<>(16);
        }
    }

}
