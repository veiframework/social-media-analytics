package com.chargehub.common.core.mq;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/08/19 15:48
 */
public interface MqProducer {

    default void send(String producer, Object params) {
        send(producer, params, false);
    }

    default List<Object> syncSend(String producer, Object params) {
        return send(producer, params, true);
    }

    List<Object> send(String producer, Object params, boolean sync);

    void convertAndSend(String group, String topic, final Object object);

}
