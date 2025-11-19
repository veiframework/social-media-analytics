package com.chargehub.common.rocketmq;

import com.chargehub.common.core.mq.MessageQueueProducer;
import com.chargehub.common.core.properties.ChargeHubProperties;
import com.chargehub.common.core.properties.RocketProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/08/19 18:00
 */
@Slf4j
@Configuration
public class RocketMqConfig {

    private final ChargeHubProperties rocketMqProperties;

    private final RocketMQMessageConverter rocketMQMessageConverter;

    private final RocketMQProperties apacheRocketProperties;


    public RocketMqConfig(ChargeHubProperties chargeHubProperties,
                          RocketMQMessageConverter rocketMQMessageConverter,
                          RocketMQProperties apacheRocketProperties) {
        this.rocketMqProperties = chargeHubProperties;
        this.rocketMQMessageConverter = rocketMQMessageConverter;
        this.apacheRocketProperties = apacheRocketProperties;
    }


    public DefaultMQProducer defaultMQProducer(RocketMQProperties rocketMQProperties,
                                               RocketProducer producerConfig) {
        String nameServer = rocketMQProperties.getNameServer();
        String groupName = producerConfig.getGroup();
        if (org.apache.commons.lang3.StringUtils.isBlank(groupName)) {
            groupName = producerConfig.getTopic();
        }
        Integer queueNum = producerConfig.getQueueNum();
        Assert.notNull(queueNum, "[rocketmq.queue-num] must not be null");
        Assert.hasText(nameServer, "[rocketmq.name-server] must not be null");
        Assert.hasText(groupName, "[rocketmq.producer.group] must not be null");

        String accessChannel = rocketMQProperties.getAccessChannel();

        String ak = producerConfig.getAccessKey();
        String sk = producerConfig.getSecretKey();
        if (StringUtils.isBlank(ak)) {
            RocketMQProperties.Producer defaultProducer = rocketMQProperties.getProducer();
            if (defaultProducer != null) {
                ak = defaultProducer.getAccessKey();
            }
        }
        if (StringUtils.isBlank(sk)) {
            RocketMQProperties.Producer defaultProducer = rocketMQProperties.getProducer();
            if (defaultProducer != null) {
                sk = defaultProducer.getSecretKey();
            }
        }
        boolean isEnableMsgTrace = producerConfig.isEnableMsgTrace();
        String customizedTraceTopic = producerConfig.getCustomizedTraceTopic();

        DefaultMQProducer producer = RocketMQUtil.createDefaultMQProducer(groupName, ak, sk, isEnableMsgTrace, customizedTraceTopic);

        producer.setNamesrvAddr(nameServer);
        if (StringUtils.isNotBlank(accessChannel)) {
            producer.setAccessChannel(AccessChannel.valueOf(accessChannel));
        }
        producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
        producer.setUseTLS(producerConfig.isTlsEnable());
        if (StringUtils.isNotBlank(producerConfig.getNamespace())) {
            producer.setNamespace(producerConfig.getNamespace());
        }
        if (StringUtils.isNotBlank(producerConfig.getNamespaceV2())) {
            producer.setNamespaceV2(producerConfig.getNamespaceV2());
        }
        producer.setInstanceName(producerConfig.getInstanceName());
        producer.setDefaultTopicQueueNums(queueNum);
        log.info("a producer ({}) init on namesrv {}", groupName, nameServer);
        return producer;
    }


    public RocketMQTemplate rocketMQTemplate(DefaultMQProducer defaultMQProducer,
                                             DefaultLitePullConsumer defaultLitePullConsumer) {
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(defaultMQProducer);
        rocketMQTemplate.setConsumer(defaultLitePullConsumer);
        rocketMQTemplate.setMessageConverter(rocketMQMessageConverter.getMessageConverter());
        return rocketMQTemplate;
    }

    @Primary
    @Bean
    public RocketMqProducer rocketMqProducer(List<MessageQueueProducer<?>> producers) {
        RocketMqProducer rocketMqProducer = new RocketMqProducer(producers);
        Map<String, RocketProducer> producerConfigs = rocketMqProperties.getRocket();

        producerConfigs.forEach((k, v) -> {
            if (!v.isInit()) {
                return;
            }
            try {
                DefaultMQProducer defaultMQProducer = defaultMQProducer(apacheRocketProperties, v);
                RocketMQTemplate rocketMQTemplate = rocketMQTemplate(defaultMQProducer, null);
                rocketMQTemplate.afterPropertiesSet();
                rocketMqProducer.addProducer(defaultMQProducer.getProducerGroup(), rocketMQTemplate);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        });
        return rocketMqProducer;
    }


}
