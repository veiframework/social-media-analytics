package com.chargehub.common.redis.core;

import org.redisson.config.Config;

/**
 * @author Zhanghaowei
 * @date 2022/7/5
 */
@FunctionalInterface
public interface RedissonConfigurationCustomizer {
    /**
     * 自定义Redisson配置
     *
     * @param config
     * @author Zhanghaowei
     */
    void customize(Config config);
}