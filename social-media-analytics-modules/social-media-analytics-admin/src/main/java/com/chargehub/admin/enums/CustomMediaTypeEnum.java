package com.chargehub.admin.enums;

import lombok.Getter;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Getter
public enum CustomMediaTypeEnum {

    SHARE("share", "好物分享"),

    SIGN("sign", "打卡"),

    CHANGE("change", "爆改");

    private final String type;
    private final String desc;

    CustomMediaTypeEnum(String type, String desc) {
        this.desc = desc;
        this.type = type;
    }

    public static CustomMediaTypeEnum getByType(String type) {
        for (CustomMediaTypeEnum value : CustomMediaTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static CustomMediaTypeEnum getByDesc(String desc) {
        for (CustomMediaTypeEnum value : CustomMediaTypeEnum.values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        return null;
    }

    public static CustomMediaTypeEnum containsDesc(String desc) {
        for (CustomMediaTypeEnum value : CustomMediaTypeEnum.values()) {
            if (desc.contains(value.getDesc())) {
                return value;
            }
        }
        return null;
    }

}
