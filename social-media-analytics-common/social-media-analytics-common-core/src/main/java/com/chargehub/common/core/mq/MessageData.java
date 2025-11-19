package com.chargehub.common.core.mq;

import com.alibaba.fastjson2.JSON;
import com.chargehub.common.core.enums.MessageCodeEnums;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * MessageBody 消息队列
 * date 2023/07/25
 *
 * @author TiAmo(13721682347 @ 163.com)
 */
public class MessageData<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 幂等Id
     */
    private String messageId;

    /**
     * businessCode
     */
    private String businessCode;

    /**
     * businessId
     */
    private String businessId;

    /**
     * 数据时间
     */
    private LocalDateTime dateTime;

    /**
     * 设备编码
     */
    private String idCode;

    /**
     * 元数据
     */
    private T data;


    /**
     * 充电设备接口ID
     */
    private String connectorId;


    public MessageData(MessageCodeEnums codeEnum, T data) {
        this.businessCode = codeEnum.getCode();
        this.data = data;
    }

    /**
     * 转成json
     *
     * @return
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public MessageData() {
    }
}
