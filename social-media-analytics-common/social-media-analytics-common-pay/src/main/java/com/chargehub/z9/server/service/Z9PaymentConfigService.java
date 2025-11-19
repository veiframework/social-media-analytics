package com.chargehub.z9.server.service;



import com.chargehub.common.security.template.service.Z9CrudService;
import com.chargehub.z9.server.domain.Z9PaymentConfig;

import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/05/19 17:06
 */
public interface Z9PaymentConfigService extends Z9CrudService<Z9PaymentConfig> {


    /**
     * 根据租户id获取支付配置
     *
     * @param configClass
     * @param params
     * @return
     */
    <R> R getConfigByExtendParam(Class<R> configClass, Map<String, String> params);

    /**
     * 根据配置id获取配置
     *
     * @param configClass
     * @param configId
     * @param <R>
     * @return
     */
    <R> R getByConfigId(Class<R> configClass, String configId);
}
