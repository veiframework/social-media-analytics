package com.chargehub.common.core.mq;

/**
 * @author Zhanghaowei
 * @date 2024/08/20 19:26
 */
public interface MqConsumer {

    String topic();

    String group();

    boolean reply();
}
