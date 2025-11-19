package com.chargehub.common.datasource.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/04/04 10:54
 */
@Configuration
public class MybatisPlusConfiguration {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(List<InnerInterceptor> innerInterceptors) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.setInterceptors(innerInterceptors);
        return interceptor;
    }

    /**
     * mybatis-plus分页插件
     *
     * @return
     */
    @Order
    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor();
    }

}
