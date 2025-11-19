package com.vchaoxi.logistic;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Zhanghaowei
 * @date 2025/08/25 15:16
 */
@Slf4j
@ComponentScan({"com.vchaoxi.logistic"})
@MapperScan({"com.vchaoxi.logistic.mapper"})
public class LogisticConfiguration {


}
