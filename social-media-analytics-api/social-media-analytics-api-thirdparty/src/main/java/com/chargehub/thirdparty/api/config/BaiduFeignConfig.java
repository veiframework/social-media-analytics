package com.chargehub.thirdparty.api.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @description: 百度
 * @author: lfy
 * @create: 2024-09-10 16:38
 */
public class BaiduFeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // 或者 Logger.Level.DEBUG
    }
}
