package com.chargehub.admin.enums;

import lombok.Getter;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Getter
public enum WorkPriorityEnum {

    IMPORTANT(1),
    ACTIVE(2),
    NORMAL(3),
    DOCUMENT(4);

    private final int code;

    WorkPriorityEnum(int code) {
        this.code = code;
    }

}
