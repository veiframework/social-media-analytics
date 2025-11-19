package com.chargehub.common.knife4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author Zhanghaowei
 * @date 2024/04/08 18:58
 */
public class Swagger3AutoConfig {

    @Value("${spring.application.name:unknown}")
    private String application;


    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info().title(application).version("1.0.0"));
    }

    @Bean
    @ConditionalOnMissingBean
    public GroupedOpenApi defaultGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group(application)
                .pathsToMatch("/**")
                .addOpenApiMethodFilter(method -> method.isAnnotationPresent(Operation.class))
                .build();
    }

}
