package com.chargehub.common.rabbitmq;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author Zhanghaowei
 * @date 2024/04/12 18:01
 */
public interface RabbitMqConsumer<T> {

    Logger log = LoggerFactory.getLogger(RabbitMqConsumer.class);


    default String describe() {
        return "";
    }

    /**
     * 消费
     *
     * @param t
     * @param deliveryTag
     * @param channel
     */
    default void onMessage(T t, @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag, Channel channel) {
        StopWatch stopWatch = null;
        String timerName = describe();
        boolean hasTimer = StringUtils.hasText(timerName);
        if (hasTimer) {
            stopWatch = new StopWatch(describe());
            stopWatch.start();
        }
        try {
            this.onMessage(t, channel);
        } catch (Exception ex) {
            log.error("rabbitmq error:", ex);
        } finally {
            try {
                /**
                 * deliveryTag:该消息的index
                 * multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
                 * requeue：被拒绝的是否重新入队列
                 */
                channel.basicAck(deliveryTag, false);
            } catch (IOException ex) {
                log.error("rabbitmq ack error:", ex);
            }
            if (hasTimer) {
                stopWatch.stop();
                log.info("{}处理mq消费耗时--{}", timerName, stopWatch.getTotalTimeMillis());
            }
        }

    }

    /**
     * 消费
     *
     * @param t
     * @param channel
     */
    void onMessage(T t, Channel channel);

}
