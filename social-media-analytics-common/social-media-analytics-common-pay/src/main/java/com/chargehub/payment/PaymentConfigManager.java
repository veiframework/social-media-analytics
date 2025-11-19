package com.chargehub.payment;

import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/04/03 16:05
 */
public interface PaymentConfigManager {


    /**
     * 获取配置
     *
     * @param extendParam
     * @param configClass
     * @return
     */
    <R> R getConfig(Class<R> configClass, Map<String, String> extendParam);

    /**
     * 获取配置
     *
     * @param configId
     * @param configClass
     * @return
     */
    <R> R getConfig(Class<R> configClass, String configId);
}
