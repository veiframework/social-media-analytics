package com.chargehub.z9.server.service;

import com.chargehub.payment.PaymentConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/05/19 17:04
 */
@Slf4j
@Service
public class Z9PaymentConfigManager implements PaymentConfigManager {



    private final Z9PaymentConfigService z9PaymentConfigService;

    public Z9PaymentConfigManager(Z9PaymentConfigService z9PaymentConfigService) {
        this.z9PaymentConfigService = z9PaymentConfigService;
        log.info(" [Z9] use z9 payment config manager.");
    }

    @Override
    public <R> R getConfig(Class<R> configClass, Map<String, String> extendParam) {
        return z9PaymentConfigService.getConfigByExtendParam(configClass, extendParam);
    }

    @Override
    public <R> R getConfig(Class<R> configClass, String configId) {
        return z9PaymentConfigService.getByConfigId(configClass, configId);
    }


}
