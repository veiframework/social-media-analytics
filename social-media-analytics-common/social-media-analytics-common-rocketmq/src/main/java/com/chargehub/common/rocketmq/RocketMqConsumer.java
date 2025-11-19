package com.chargehub.common.rocketmq;

import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

/**
 * @author Zhanghaowei
 * @date 2024/08/21 09:48
 */
public interface RocketMqConsumer<T> extends RocketMQListener<T>, Ordered {

    Logger log = LoggerFactory.getLogger(RocketMqConsumer.class);


    default String describe() {
        return "";
    }


    @Override
    default void onMessage(T message) {
        StopWatch stopWatch = null;
        String timerName = describe();
        boolean hasTimer = StringUtils.hasText(timerName);
        if (hasTimer) {
            stopWatch = new StopWatch(describe());
            stopWatch.start();
        }
        try {
            this.consume(message);
        } catch (Exception ex) {
            log.error("rabbitmq error:", ex);
        } finally {
            if (hasTimer) {
                stopWatch.stop();
                log.info("{}处理mq消费耗时--{}", timerName, stopWatch.getTotalTimeMillis());
            }
        }
    }

    void consume(T message);

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
