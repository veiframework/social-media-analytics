package com.chargehub.thirdparty.config.baidu;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({BaiduMPProperties.class})
public class BaiduMPConfiguration {

}