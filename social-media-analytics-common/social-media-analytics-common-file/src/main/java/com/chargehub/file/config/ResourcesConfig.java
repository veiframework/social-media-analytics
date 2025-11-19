package com.chargehub.file.config;

import com.chargehub.common.core.constant.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 通用映射配置
 * 
 * @author ruoyi
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer
{
    /**
     * 上传文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getLocalFilePrefix() {
        return localFilePrefix;
    }

    public void setLocalFilePrefix(String localFilePrefix) {
        this.localFilePrefix = localFilePrefix;
    }

    /**
     * 资源映射路径 前缀
     */
    @Value("${file.prefix}")
    public String localFilePrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        /** 本地文件上传路径 */
        registry.addResourceHandler(localFilePrefix + "/**")
                .addResourceLocations("file:" + localFilePath + File.separator);
    }
    
    /**
     * 开启跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路由
        CorsRegistration registration = registry.addMapping(Constants.ALL_PATH);
        registration.allowCredentials(true)
                .allowedOriginPatterns(Constants.ASTERISK)
                .allowedMethods(Constants.ALLOWED_METHOD.toArray(new String[0]))
                .exposedHeaders(Constants.EXPOSED_HEADERS.toArray(new String[0]))
                .allowedHeaders(Constants.ALLOWED_HEADERS.toArray(new String[0]))
                .maxAge(3600L);
    }
}