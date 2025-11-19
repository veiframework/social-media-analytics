package com.chargehub.common.security.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.chargehub.common.redis.service.ValidateCodeService;
import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.handler.WebValidateCodeHandler;
import com.chargehub.common.security.interceptor.ValidationCodeFilter;
import com.chargehub.common.security.mapper.DefaultMetaObjectHandler;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.service.RemoteAsyncLogServiceImpl;
import com.chargehub.log.api.RemoteLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 系统配置
 *
 * @author ruoyi
 */
public class ApplicationConfig {
    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter HMS = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter Y_M_D = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);
    public static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 时区配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return (jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
            jacksonObjectMapperBuilder.modules(getTimeModule());
        });
    }


    public static JavaTimeModule getTimeModule() {

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //HH:mm:ss
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(HMS));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(HMS));
        //yyyy-MM-dd
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(YMD));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(YMD));
        //yyyy-MM-dd HH:mm:ss
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(Y_M_D));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Y_M_D));
        //date类型
        javaTimeModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(NORM_DATETIME_PATTERN)));
        javaTimeModule.addDeserializer(Date.class, new DateDeserializers.DateDeserializer(new DateDeserializers.DateDeserializer(), new SimpleDateFormat(NORM_DATETIME_PATTERN), NORM_DATETIME_PATTERN));
        return javaTimeModule;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new DefaultMetaObjectHandler();
    }

    @ConditionalOnMissingBean
    @Bean
    public ChargeExcelDictHandler chargeExcelDictHandler() {
        return new ChargeExcelDictHandler();
    }

    @ConditionalOnMissingClass("com.chargehub.common.log.service.impl.AsyncLogServiceImpl")
    @Bean
    public RemoteAsyncLogServiceImpl remoteAsyncLogService(RemoteLogService remoteLogService) {
        return new RemoteAsyncLogServiceImpl(remoteLogService);
    }


    @ConditionalOnProperty(prefix = "hub", name = "standalone", havingValue = "true")
    @Bean
    public RouterFunction<ServerResponse> validateCodeFunction(ValidateCodeService validateCodeService) {
        return RouterFunctions.route().GET("/code", new WebValidateCodeHandler(validateCodeService)).build();
    }

    @Order(1)
    @ConditionalOnProperty(prefix = "hub", name = "standalone", havingValue = "true")
    @Bean
    public Filter validateCodeFilter(ValidateCodeService validateCodeService,
                                     ObjectMapper objectMapper) {
        return new ValidationCodeFilter(validateCodeService, objectMapper);
    }

}
