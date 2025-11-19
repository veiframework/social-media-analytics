package com.chargehub.thirdparty;

import com.chargehub.common.security.annotation.EnableCustomConfig;
import com.chargehub.common.security.annotation.EnableRyFeignClients;
import com.chargehub.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 第三方服务
 * 
 * @author ruoyi
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication(scanBasePackages = "com.chargehub")
public class ThirdPartyApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ThirdPartyApplication.class, args);
        System.out.println(" Third party 第三方服务 启动成功...");
    }
}
