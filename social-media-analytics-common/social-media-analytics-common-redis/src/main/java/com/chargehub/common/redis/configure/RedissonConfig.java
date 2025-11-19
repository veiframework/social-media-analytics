package com.chargehub.common.redis.configure;

import com.chargehub.common.redis.core.Mode;
import com.chargehub.common.redis.core.RedissonConfigurationCustomizer;
import com.chargehub.common.redis.core.RedissonProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/04/23 19:14
 */
@ConditionalOnProperty(prefix = "spring.redis.redisson", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(RedissonClient.class)
@EnableConfigurationProperties(RedissonProperties.class)
@Slf4j
@Configuration
public class RedissonConfig {
    public static final String MODE = "spring.redis.mode";
    private static final String REDIS_PROTOCOL_PREFIX = "redis://";
    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";


    @Lazy
    @Bean(destroyMethod = "shutdown")
    @Primary
    @ConditionalOnMissingBean({RedissonClient.class})
    public RedissonClient redisson(Environment environment, RedisProperties properties,
                                   RedissonProperties redissonProperties,
                                   List<RedissonConfigurationCustomizer> redissonConfigurationCustomizers,
                                   ObjectMapper objectMapper) {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec(objectMapper));
        int timeout = getTimeout(properties.getTimeout());
        Mode mode = environment.getProperty(MODE, Mode.class, Mode.STANDALONE);
        switch (mode) {
            case SENTINEL:
                RedisProperties.Sentinel sentinel = properties.getSentinel();
                List<String> nodes = sentinel.getNodes();
                SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                        .setMasterName(sentinel.getMaster())
                        .setDatabase(properties.getDatabase())
                        .setConnectTimeout(timeout)
                        .setKeepAlive(true)
                        .setPingConnectionInterval(redissonProperties.getPingConnectionInterval())
                        .setMasterConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                        .setMasterConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                        .setSlaveConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                        .setSlaveConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                        .addSentinelAddress(convert(nodes));
                if (StringUtils.isNotBlank(sentinel.getPassword())) {
                    sentinelServersConfig.setPassword(sentinel.getPassword());
                }
                break;
            case CLUSTER:
                RedisProperties.Cluster cluster = properties.getCluster();
                ClusterServersConfig clusterServersConfig = config.useClusterServers()
                        .setConnectTimeout(timeout)
                        .setKeepAlive(true)
                        .setPingConnectionInterval(redissonProperties.getPingConnectionInterval())
                        .setMasterConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                        .setMasterConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                        .setSlaveConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                        .setSlaveConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                        .addNodeAddress(convert(cluster.getNodes()));

                if (StringUtils.isNotBlank(properties.getPassword())) {
                    clusterServersConfig.setPassword(properties.getPassword());
                }
                break;
            default:
                String prefix = properties.isSsl() ? REDISS_PROTOCOL_PREFIX : REDIS_PROTOCOL_PREFIX;
                SingleServerConfig singleServerConfig = config.useSingleServer()
                        .setAddress(prefix + properties.getHost() + StringPool.COLON + properties.getPort())
                        .setConnectTimeout(timeout)
                        .setKeepAlive(true)
                        .setPingConnectionInterval(redissonProperties.getPingConnectionInterval())
                        .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
                        .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                        .setDatabase(redissonProperties.getDatabase());
                if (StringUtils.isNotBlank(properties.getPassword())) {
                    singleServerConfig.setPassword(properties.getPassword());
                }
        }
        if (redissonConfigurationCustomizers != null) {
            for (RedissonConfigurationCustomizer customizer : redissonConfigurationCustomizers) {
                customizer.customize(config);
            }
        }
        return Redisson.create(config);
    }


    @Bean
    @ConditionalOnMissingBean({RedisConnectionFactory.class})
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    private static int getTimeout(Duration timeout) {
        if (timeout != null && timeout.toMillis() < Integer.MAX_VALUE) {
            return (int) timeout.toMillis();
        }
        return 10000;
    }

    private static String[] convert(List<String> nodesObject) {
        List<String> nodes = new ArrayList<>(nodesObject.size());
        for (String node : nodesObject) {
            if (!node.startsWith(REDIS_PROTOCOL_PREFIX) && !node.startsWith(REDISS_PROTOCOL_PREFIX)) {
                nodes.add(REDIS_PROTOCOL_PREFIX + node);
            } else {
                nodes.add(node);
            }
        }
        return nodes.toArray(new String[0]);
    }

}
