package com.chargehub.common.rabbitmq;

import cn.hutool.core.util.IdUtil;
import com.chargehub.common.core.mq.MessageQueueDto;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/04/12 10:25
 */
public class RabbitProducerMessagePostProcessor implements MessagePostProcessor {


    private Map<String, Object> headers;

    private int delay;

    public RabbitProducerMessagePostProcessor(MessageQueueDto messageQueueDto) {
        this.headers = messageQueueDto.headers();
        this.delay = messageQueueDto.getDelay();
    }

    public RabbitProducerMessagePostProcessor() {
    }


    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        message.getMessageProperties().setHeaders(headers);
        message.getMessageProperties().setMessageId(IdUtil.simpleUUID());
        if (delay > 0) {
            message.getMessageProperties().setDelay(delay);
        }
        return message;
    }
}
