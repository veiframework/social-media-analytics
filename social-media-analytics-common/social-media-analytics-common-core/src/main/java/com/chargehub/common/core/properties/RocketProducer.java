package com.chargehub.common.core.properties;

/**
 * @author Zhanghaowei
 * @date 2024/08/20 15:35
 */
public class RocketProducer {

    private boolean init = true;

    private Integer queueNum = 4;

    private String topic;


    /**
     * Group name of producer.
     */
    private String group;

    /**
     * Namespace for this MQ Producer instance.
     */
    private String namespace;

    /**
     * The namespace v2 version of producer, it can not be used in combination with namespace.
     */
    private String namespaceV2;

    /**
     * Millis of send message timeout.
     */
    private int sendMessageTimeout = 10000;

    /**
     * Compress message body threshold, namely, message body larger than 4k will be compressed on default.
     */
    private int compressMessageBodyThreshold = 1024 * 4;

    /**
     * Maximum number of retry to perform internally before claiming sending failure in synchronous mode.
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    private int retryTimesWhenSendFailed = 2;

    /**
     * <p> Maximum number of retry to perform internally before claiming sending failure in asynchronous mode. </p>
     * This may potentially cause message duplication which is up to application developers to resolve.
     */
    private int retryTimesWhenSendAsyncFailed = 2;

    /**
     * Indicate whether to retry another broker on sending failure internally.
     */
    private boolean retryNextServer = false;

    /**
     * Maximum allowed message size in bytes.
     */
    private int maxMessageSize = 1024 * 1024 * 4;

    /**
     * The property of "access-key".
     */
    private String accessKey;

    /**
     * The property of "secret-key".
     */
    private String secretKey;

    /**
     * Switch flag instance for message trace.
     */
    private boolean enableMsgTrace = false;

    /**
     * The name value of message trace topic.If you don't config,you can use the default trace topic name.
     */
    private String customizedTraceTopic = "RMQ_SYS_TRACE_TOPIC";

    /**
     * The property of "tlsEnable".
     */
    private boolean tlsEnable = false;

    /**
     * The property of "instanceName".
     */
    private String instanceName = "DEFAULT";

    public Integer getQueueNum() {
        return queueNum;
    }

    public void setQueueNum(Integer queueNum) {
        this.queueNum = queueNum;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespaceV2() {
        return namespaceV2;
    }

    public void setNamespaceV2(String namespaceV2) {
        this.namespaceV2 = namespaceV2;
    }

    public int getSendMessageTimeout() {
        return sendMessageTimeout;
    }

    public void setSendMessageTimeout(int sendMessageTimeout) {
        this.sendMessageTimeout = sendMessageTimeout;
    }

    public int getCompressMessageBodyThreshold() {
        return compressMessageBodyThreshold;
    }

    public void setCompressMessageBodyThreshold(int compressMessageBodyThreshold) {
        this.compressMessageBodyThreshold = compressMessageBodyThreshold;
    }

    public int getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }

    public void setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) {
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
    }

    public int getRetryTimesWhenSendAsyncFailed() {
        return retryTimesWhenSendAsyncFailed;
    }

    public void setRetryTimesWhenSendAsyncFailed(int retryTimesWhenSendAsyncFailed) {
        this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
    }

    public boolean isRetryNextServer() {
        return retryNextServer;
    }

    public void setRetryNextServer(boolean retryNextServer) {
        this.retryNextServer = retryNextServer;
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isEnableMsgTrace() {
        return enableMsgTrace;
    }

    public void setEnableMsgTrace(boolean enableMsgTrace) {
        this.enableMsgTrace = enableMsgTrace;
    }

    public String getCustomizedTraceTopic() {
        return customizedTraceTopic;
    }

    public void setCustomizedTraceTopic(String customizedTraceTopic) {
        this.customizedTraceTopic = customizedTraceTopic;
    }

    public boolean isTlsEnable() {
        return tlsEnable;
    }

    public void setTlsEnable(boolean tlsEnable) {
        this.tlsEnable = tlsEnable;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
