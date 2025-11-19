package com.chargehub.common.redis.core;

/**
 * @author Zhanghaowei
 * @since 2024-01-19 15:35
 */
public enum Mode {

    /**
     * 哨兵模式
     */
    SENTINEL,
    /**
     * 集群模式
     */
    CLUSTER,
    /**
     * 单机模式
     */
    STANDALONE

}
