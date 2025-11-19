package com.chargehub.common.rabbitmq;

import cn.hutool.core.lang.Assert;
import com.chargehub.common.core.enums.MessageCodeEnums;
import com.chargehub.common.core.mq.MessageData;
import com.chargehub.common.core.mq.MessageQueueDto;
import com.chargehub.common.core.mq.MessageQueueProducer;
import com.chargehub.common.core.mq.MqProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2024/04/12 09:55
 */
@Slf4j
public class RabbitMqProducer implements InitializingBean, RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback, MqProducer {


    private final ConnectionFactory connectionFactory;

    private RabbitTemplate rabbitTemplate;


    private final Map<String, MessageQueueProducer<Object>> producerMap;

    @SuppressWarnings("unchecked")
    public RabbitMqProducer(ConnectionFactory connectionFactory, List<MessageQueueProducer<?>> producers) {
        this.connectionFactory = connectionFactory;
        this.producerMap = producers.stream().collect(Collectors.toMap(MessageQueueProducer::producer, v -> (MessageQueueProducer<Object>) v));
    }

    public void send(String producer, Object params) {
        send(producer, params, false);
    }

    /**
     * 同步发消息
     * TODO 等桩通信迁移完毕后部分消息改为同步获取消息
     *
     * @param producer
     * @param params
     * @return
     */
    public List<Object> syncSend(String producer, Object params) {
        return send(producer, params, true);
    }

    /**
     * TODO 递归发送mq消息,等后期迁移桩通信完毕后改成批处理
     *
     * @param sync
     * @param producer
     * @param params
     * @return
     */
    public List<Object> send(String producer, Object params, boolean sync) {
        MessageQueueProducer<Object> messageQueueProducer = producerMap.get(producer);
        if (messageQueueProducer == null) {
            log.info(" 尚未匹配到消息生产者:{}", producer);
            return Collections.emptyList();
        }
        String routingKey = messageQueueProducer.routingKey();
        String exchange = messageQueueProducer.exchange();
        Assert.isFalse(StringUtils.isBlank(routingKey) && StringUtils.isBlank(exchange), "交换机和队列至少声明其中一个");
        List<? extends MessageQueueDto> messageQueueDtos = messageQueueProducer.buildMessage(params);
        if (CollectionUtils.isEmpty(messageQueueDtos)) {
            return Collections.emptyList();
        }
        List<Object> results = new ArrayList<>();
        for (MessageQueueDto messageQueueDto : messageQueueDtos) {
            Object message = sendMessage(exchange, routingKey, messageQueueDto, messageQueueProducer, sync);
            if (message == null) {
                continue;
            }
            results.add(message);
        }
        return results;
    }

    @Override
    public void convertAndSend(String group, String topic, Object object) {
        rabbitTemplate.convertAndSend(group, topic, object);
    }


    private Object sendMessage(String exchange, String routingKey, MessageQueueDto messageQueueDto, MessageQueueProducer<Object> messageQueueProducer, boolean sync) {
        MessageCodeEnums code = messageQueueProducer.code();
        Object param;
        if (code == null) {
            //没有编码类型则默认对象传输
            Object originalData = messageQueueDto.originalData();
            Assert.notNull(originalData, "原始数据传输不得为空");
            param = originalData;
        } else {
            MessageData<Object> messageData;
            Object messageTargetData = messageQueueDto.messageTargetData();
            if (messageTargetData != null) {
                messageData = new MessageData<>(code, messageTargetData);
            } else {
                messageData = new MessageData<>(code, messageQueueDto);
            }
            //TODO 目前为了兼容桩通信,暂时采用json传输,等项目迁移完毕改为标准对象传输
            if (messageQueueDto.originalData() == null) {
                param = messageData.toJson();
            } else {
                param = messageQueueDto.originalData();
            }
        }
        if (sync) {
            return rabbitTemplate.convertSendAndReceive(exchange, routingKey, String.valueOf(param).getBytes(), new RabbitProducerMessagePostProcessor(messageQueueDto));
        } else {
            rabbitTemplate.convertAndSend(exchange, routingKey, String.valueOf(param).getBytes(), new RabbitProducerMessagePostProcessor(messageQueueDto));
            return null;
        }
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (!ack) {
            log.info("消息唯一标识: {}, 确认状态: {}, 造成原因: {}", correlationData, false, cause);
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("returnedMessage:{}", returnedMessage);
    }

    @Override
    public void afterPropertiesSet() {
        rabbitTemplate = new RabbitTemplate(this.connectionFactory);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }


}
