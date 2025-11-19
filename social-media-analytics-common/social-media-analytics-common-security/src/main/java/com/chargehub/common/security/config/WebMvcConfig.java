package com.chargehub.common.security.config;

import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.security.interceptor.HeaderInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 *
 * @author ruoyi
 */
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 不需要拦截地址
     */
    public static final String[] excludeUrls = {"/login", "/logout", "/refresh"};

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getHeaderInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrls)
                .order(-10);
    }

    /**
     * 自定义请求头拦截器
     */
    public HeaderInterceptor getHeaderInterceptor() {
        return new HeaderInterceptor(hubProperties);
    }

    @Autowired
    private HubProperties hubProperties;


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        if (!hubProperties.isStandalone()) {
            return;
        }
        configurer.setUrlPathHelper(new CustomUrlPathHelper());
    }

}
