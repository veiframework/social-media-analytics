package com.chargehub.common.core.mq;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/04/15 10:02
 */
public interface MessageQueueDto {


    /**
     * 消息头额外参数
     *
     * @return
     */
    default Map<String, Object> headers() {
        return new HashMap<>(16);
    }

    /**
     * 是否使用原始数据
     *
     * @return
     */
    default Object originalData() {
        return null;
    }

    /**
     * 延迟时间
     *
     * @return
     */
    default int getDelay() {
        return 0;
    }


    default Object messageTargetData(){
        return null;
    }

}
