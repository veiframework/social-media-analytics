package com.chargehub.admin.enums;

import lombok.Getter;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Getter
public enum WorkCreateStatusEnum {

    WAIT("wait"),
    PROCESSING("processing"),
    SUCCESS("success"),
    FAIL("fail");

    private final String desc;

    WorkCreateStatusEnum(String desc) {
        this.desc = desc;
    }


}
