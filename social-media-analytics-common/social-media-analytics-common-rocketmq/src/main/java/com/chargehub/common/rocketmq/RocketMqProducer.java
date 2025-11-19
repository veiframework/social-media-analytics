package com.chargehub.common.rocketmq;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.chargehub.common.core.enums.MessageCodeEnums;
import com.chargehub.common.core.mq.MessageData;
import com.chargehub.common.core.mq.MessageQueueDto;
import com.chargehub.common.core.mq.MessageQueueProducer;
import com.chargehub.common.core.mq.MqProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2024/08/15 19:06
 */
@Slf4j
public class RocketMqProducer implements MqProducer {


    private static final Map<String, RocketMQTemplate> PRODUCER_EXECUTORS = new HashMap<>();

    private final Map<String, MessageQueueProducer<Object>> producerMap;

    @SuppressWarnings("unchecked")
    public RocketMqProducer(List<MessageQueueProducer<?>> producers) {
        this.producerMap = producers.stream().collect(Collectors.toMap(MessageQueueProducer::producer, v -> (MessageQueueProducer<Object>) v));
    }

    public void addProducer(String group, RocketMQTemplate rocketMQTemplate) {
        PRODUCER_EXECUTORS.put(group, rocketMQTemplate);
    }

    public void send(String producer, Object params) {
        send(producer, params, false);
    }


    public List<Object> syncSend(String producer, Object params) {
        return send(producer, params, true);
    }

    public List<Object> send(String producer, Object params, boolean sync) {
        MessageQueueProducer<Object> objectRocketQueueProducer = producerMap.get(producer);
        List<? extends MessageQueueDto> rocketMessageDtos = objectRocketQueueProducer.buildMessage(params);
        if (CollectionUtils.isEmpty(rocketMessageDtos)) {
            return new ArrayList<>();
        }
        List<Object> results = new ArrayList<>();
        for (MessageQueueDto rocketMessageDto : rocketMessageDtos) {
            Object message = this.sendMessage(rocketMessageDto, objectRocketQueueProducer, sync);
            if (message == null) {
                continue;
            }
            results.add(message);
        }
        return results;
    }

    @Override
    public void convertAndSend(String group, String topic, Object object) {
        if (StringUtils.isBlank(group)) {
            group = topic;
        }
        RocketMQTemplate rocketMQTemplate = PRODUCER_EXECUTORS.get(group);
        rocketMQTemplate.convertAndSend(topic, object);
    }

    private Object sendMessage(MessageQueueDto messageQueueDto, MessageQueueProducer<Object> messageQueueProducer, boolean sync) {
        int delay = messageQueueDto.getDelay();
        Map<String, Object> headers = messageQueueDto.headers();
        String group = messageQueueProducer.group();
        String topic = messageQueueProducer.topic();

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

        RocketMQTemplate rocketMQTemplate = PRODUCER_EXECUTORS.get(group);
        String json = String.valueOf(param);
        MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(json);
        if (MapUtil.isNotEmpty(headers)) {
            headers.forEach(messageBuilder::setHeaderIfAbsent);
        }
        Message<String> build = messageBuilder.build();
        if (sync) {
            return rocketMQTemplate.syncSendDelayTimeMills(topic, build, delay);
        } else {
            if (delay > 0) {
                rocketMQTemplate.syncSendDelayTimeMills(topic, build, delay);
            } else {
                rocketMQTemplate.asyncSend(topic, build, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        //noting to do
                    }

                    @Override
                    public void onException(Throwable e) {
                        //noting to do
                    }
                });
            }
            return null;
        }
    }

}
