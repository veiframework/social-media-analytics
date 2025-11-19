package com.chargehub.log;

import com.chargehub.common.security.annotation.EnableCustomConfig;
import com.chargehub.common.security.annotation.EnableRyFeignClients;
import com.chargehub.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户管理服务
 * 
 * @author ruoyi
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication(scanBasePackages = "com.chargehub")
public class LogApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(LogApplication.class, args);
        System.out.println(" Log 日志管理服务 启动成功...");
    }
}
