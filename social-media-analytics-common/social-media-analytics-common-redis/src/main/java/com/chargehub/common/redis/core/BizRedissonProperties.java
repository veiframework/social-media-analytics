package com.chargehub.common.redis.core;

import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BizRedissonProperties
 * date: 2024/10/31
 *
 * @author TiAmo@13721682347@163.com
 **/
@Data
@ConfigurationProperties(prefix = "chargehub.redis.redisson")
public class BizRedissonProperties {
    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 最小空闲订阅连接数
     */
    private int subscriptionConnectionMinimumIdleSize = 1;
    /**
     * 订阅连接最大池大小
     */
    private int subscriptionConnectionPoolSize = 50;
    /**
     * 最小空闲连接数
     */
    private int connectionMinimumIdleSize = 24;
    /**
     * 连接最大池大小
     */
    private int connectionPoolSize = 64;
    /**
     * 连接的数据库索引
     */
    private int database = 0;
    /**
     * PING命令发送间隔(毫秒)
     */
    private int pingConnectionInterval = 30000;

    /**
     * 模板配置
     */
    private RedisProperties redisProperties = new RedisProperties();


}