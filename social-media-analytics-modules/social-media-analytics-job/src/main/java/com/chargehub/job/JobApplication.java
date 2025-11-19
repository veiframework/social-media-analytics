package com.chargehub.job;

import com.chargehub.common.security.annotation.EnableCustomConfig;
import com.chargehub.common.security.annotation.EnableRyFeignClients;
import com.chargehub.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 定时任务
 * 
 * @author ruoyi
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients   
@SpringBootApplication(scanBasePackages = "com.chargehub")
public class JobApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(JobApplication.class, args);
        System.out.println(" Job 定时任务 启动成功...");
    }
}
