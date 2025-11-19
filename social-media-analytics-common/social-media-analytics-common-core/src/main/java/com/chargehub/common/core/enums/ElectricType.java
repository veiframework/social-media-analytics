package com.chargehub.common.core.enums;

import cn.hutool.core.lang.Assert;

import java.util.Arrays;

/**
 * @author Zhanghaowei
 * @date 2024/04/11 17:48
 */
public enum ElectricType {

    /**
     * 直流
     */
    DC("1"),

    /**
     * 交流
     */
    AC("2");

    String code;


    ElectricType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public Integer getIntCode() {
        return Integer.parseInt(this.getCode());
    }

    public static ElectricType getInstance(String electricType) {
        ElectricType type = Arrays.stream(values()).filter(i -> i.code.equals(electricType)).findFirst().orElse(null);
        Assert.notNull(type, "电流类型不存在");
        return type;
    }
}
