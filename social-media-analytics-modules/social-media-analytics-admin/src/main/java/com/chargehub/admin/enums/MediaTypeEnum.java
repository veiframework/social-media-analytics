package com.chargehub.admin.enums;

import lombok.Getter;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Getter
public enum MediaTypeEnum {


    PICTURE("picture"),
    VIDEO("video");

    private final String type;

    MediaTypeEnum(String type) {
        this.type = type;
    }

    public static MediaTypeEnum getByType(String type) {
        for (MediaTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的媒体类型");
    }


}
