package com.chargehub.thirdparty.api.domain.vo.stop;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 19:20
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.stop
 * @Filename：ReduceStopFeeVo
 */
@Data
public class ReduceStopFeeVo {


    /**
     * 返回消息体
     */
    private String jsonStr;


    /**
     * 有参构造函数
     */
    public ReduceStopFeeVo(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    /**
     * 无参构造函数
     */
    public ReduceStopFeeVo() {
    }
}
