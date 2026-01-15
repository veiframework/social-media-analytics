package com.chargehub.shop;

import com.chargehub.common.security.annotation.EnableCustomConfig;
import com.chargehub.common.security.annotation.EnableRyFeignClients;
import com.chargehub.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication(scanBasePackages = "com.chargehub")
public class ShopApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(ShopApplication.class, args);
    }

}
