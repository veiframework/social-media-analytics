package com.chargehub.payment;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhanghaowei
 * @date 2025/04/03 16:40
 */
@Slf4j
public class DefaultPaymentConfigManager implements PaymentConfigManager {


    private final Map<Class<?>, Object> propertyMap = new ConcurrentHashMap<>();


    public DefaultPaymentConfigManager(VeiPaymentProperties veiPaymentProperties) {
        Field[] fields = ReflectUtil.getFields(veiPaymentProperties.getClass());
        for (Field field : fields) {
            propertyMap.put(field.getType(), ReflectUtil.getFieldValue(veiPaymentProperties, field));
        }
        log.info(" [Vei] use default payment config manager.");
    }

    @Override
    public <R> R getConfig(Class<R> configClass, Map<String, String> extendParam) {
        Object obj = propertyMap.get(configClass);
        return configClass.cast(obj);
    }

    @Override
    public <R> R getConfig(Class<R> configClass, String configId) {
        return this.getConfig(configClass, Collections.emptyMap());
    }


}
