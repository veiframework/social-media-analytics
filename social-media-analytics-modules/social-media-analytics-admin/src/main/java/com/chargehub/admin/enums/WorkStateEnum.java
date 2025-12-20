package com.chargehub.admin.enums;

import lombok.Getter;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Getter
public enum WorkStateEnum {

    OPEN("open"),
    PRIVATE("private"),
    DELETED("deleted");

    private final String desc;

    WorkStateEnum(String desc) {
        this.desc = desc;
    }


}
