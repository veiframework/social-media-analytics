package com.chargehub.analytics;

import com.chargehub.common.security.annotation.EnableCustomConfig;
import com.chargehub.common.security.annotation.EnableRyFeignClients;
import com.chargehub.common.security.template.controller.ChargeExportController;
import com.chargehub.common.security.template.service.ChargeExportServiceImpl;
import com.chargehub.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 报表和分析服务
 *
 * @author ruoyi
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication(scanBasePackages = "com.chargehub")
@Import({ChargeExportController.class, ChargeExportServiceImpl.class})
public class AnalyticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsApplication.class, args);
        System.out.println(" Analytics 报表和分析服务 启动成功...");
    }
}
