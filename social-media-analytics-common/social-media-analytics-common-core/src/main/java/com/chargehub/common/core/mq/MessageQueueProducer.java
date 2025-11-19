package com.chargehub.common.core.mq;

import com.chargehub.common.core.enums.MessageCodeEnums;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/04/15 09:50
 */
public interface MessageQueueProducer<T> {

    /**
     * 生产者名称
     *
     * @return
     */
    String producer();


    default String group() {
        return topic();
    }

    String topic();

    /**
     * 路由键
     *
     * @return
     */
    default String routingKey() {
        return "";
    }

    /**
     * 交换机
     *
     * @return
     */
    default String exchange() {
        return "";
    }

    /**
     * 协议编码
     *
     * @return
     */
    MessageCodeEnums code();


    /**
     * 构建消息
     *
     * @param params
     * @return
     */
    @SuppressWarnings("all")
    List<? extends MessageQueueDto> buildMessage(T params);


}
