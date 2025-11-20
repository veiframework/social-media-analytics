package com.chargehub.admin.enums;

import lombok.Getter;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Getter
public enum WorkTypeEnum {

    RICH_TEXT("richText"),
    NORMAL_VIDEO("normalVideo");

    private final String type;

    WorkTypeEnum(String type) {
        this.type = type;
    }

    public static WorkTypeEnum getByType(String type) {
        for (WorkTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的作品类型");
    }

}
