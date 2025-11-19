package com.chargehub.thirdparty.config.division;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({DivisionProperties.class})
public class DivisionConfiguration {
}
