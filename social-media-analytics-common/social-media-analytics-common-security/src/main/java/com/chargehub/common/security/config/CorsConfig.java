package com.chargehub.common.security.config;

import com.chargehub.common.core.constant.Constants;
import com.chargehub.common.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * @author Zhanghaowei
 * @date 2024/04/19 10:39
 */
@Slf4j
public class CorsConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        boolean corsConfig = SpringUtils.containsBean("resourcesConfig");
        if (corsConfig) {
            log.info("[corsConfig] found cors config so be it");
            return;
        }
        CorsRegistration registration = registry.addMapping(Constants.ALL_PATH);
        registration.allowCredentials(true)
                .allowedOriginPatterns(Constants.ASTERISK)
                .allowedMethods(Constants.ALLOWED_METHOD.toArray(new String[0]))
                .exposedHeaders(Constants.EXPOSED_HEADERS.toArray(new String[0]))
                .allowedHeaders(Constants.ALLOWED_HEADERS.toArray(new String[0]))
                .maxAge(3600L);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
        localeInterceptor.setParamName("lang");
        registry.addInterceptor(localeInterceptor);
    }


}