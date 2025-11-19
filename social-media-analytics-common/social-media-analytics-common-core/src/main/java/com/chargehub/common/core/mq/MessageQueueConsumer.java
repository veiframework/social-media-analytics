package com.chargehub.common.core.mq;

import com.chargehub.common.core.enums.MessageCodeEnums;

/**
 * 消息执行器
 * DATE: 2023.08.07
 *
 * @author TiAmo(13721682347 @ 163.com)
 **/
public interface MessageQueueConsumer<T> {


    /**
     * 协议编码
     *
     * @return ProtocolCodeEnum
     */
    MessageCodeEnums code();


    /**
     * 执行方法
     *
     * @param body body
     */
    void execute(MessageData<T> body);
}
