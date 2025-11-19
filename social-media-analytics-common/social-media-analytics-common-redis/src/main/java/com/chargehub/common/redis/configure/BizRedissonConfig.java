package com.chargehub.common.redis.configure;

import com.chargehub.common.redis.core.BizRedissonProperties;
import com.chargehub.common.redis.core.RedissonConfigurationCustomizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.util.List;

/**
 * @author Zhanghaowei
 * date 2024/10/31
 */
@Slf4j
@ConditionalOnProperty(prefix = "chargehub.redis.redisson", name = "enabled", havingValue = "true")
@ConditionalOnClass(RedissonClient.class)
@EnableConfigurationProperties(BizRedissonProperties.class)
@Configuration
public class BizRedissonConfig {

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";

    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";


    @Lazy
    @Bean(destroyMethod = "shutdown", name = "bizRedisson")
    public RedissonClient redisson(
                                    BizRedissonProperties redissonProperties,
                                   List<RedissonConfigurationCustomizer> redissonConfigurationCustomizers,
                                   ObjectMapper objectMapper) {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec(objectMapper));

        RedisProperties redisProperties = redissonProperties.getRedisProperties();

        int timeout = getTimeout(redisProperties.getTimeout());
        String prefix = redisProperties.isSsl() ? REDISS_PROTOCOL_PREFIX : REDIS_PROTOCOL_PREFIX;
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(prefix + redisProperties.getHost() + StringPool.COLON + redisProperties.getPort())
                .setConnectTimeout(timeout)
                .setKeepAlive(true)
                .setPingConnectionInterval(redissonProperties.getPingConnectionInterval())
                .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setDatabase(redissonProperties.getDatabase());

        if (StringUtils.isNotBlank(redisProperties.getPassword())) {
            singleServerConfig.setPassword(redisProperties.getPassword());
        }

        if (StringUtils.isNotBlank(redisProperties.getUsername())) {
            singleServerConfig.setUsername(redisProperties.getUsername());
        }

        if (redissonConfigurationCustomizers != null) {
            for (RedissonConfigurationCustomizer customizer : redissonConfigurationCustomizers) {
                customizer.customize(config);
            }
        }

        return Redisson.create(config);
    }


    private static int getTimeout(Duration timeout) {
        if (timeout != null && timeout.toMillis() < Integer.MAX_VALUE) {
            return (int) timeout.toMillis();
        }
        return 10000;
    }

}
