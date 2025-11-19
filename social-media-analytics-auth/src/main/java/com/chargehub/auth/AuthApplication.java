package com.chargehub.auth;

import com.chargehub.common.security.annotation.EnableCustomConfig;
import com.chargehub.common.security.annotation.EnableRyFeignClients;
import com.chargehub.common.security.config.ApplicationConfig;
import com.chargehub.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * 认证授权中心
 * 
 * @author ruoyi
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class }, scanBasePackages = "com.chargehub")
public class AuthApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println(" Auth 认证授权中心 启动成功...");
    }
}
