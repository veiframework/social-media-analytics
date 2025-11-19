package com.chargehub.thirdparty.api.annotation;

import java.util.Objects;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 19:30
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.annotation
 * @Filename：StopPlatform
 */
public enum StopPlatform {


    /**
     * 本系统支持充电抵停车费平台   0.暂无  1.一路停车  2.静态交通 3.洛阳停车  4.宜阳停车  5.洛阳停车(减免时长)  6.多弗
     */
    NONE(0, "暂无"),
    YILU(1, "一路停车"),
    STATIC(2, "静态交通"),
    LUOYANG(3, "洛阳停车"),
    YIYANG(4, "宜阳停车"),
    LUOYANG_SUBTRACT_DURATION(5, "洛阳停车(减免时长)"),
    DOUFOU(6, "多弗"),
    YIYANG_SUBTRACT_DURATION(7, "洛阳停车(减免时长)"),
    ;


    private final Integer code;
    private final String desc;


    StopPlatform(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }



    public static StopPlatform getType(Integer code) {
        for (StopPlatform type : values()) {
            if (Objects.equals(type.code, code)) {
                return type;
            }
        }
        return null;
    }
}
