package com.chargehub.common.core.enums;

import cn.hutool.core.lang.Assert;

import java.util.Arrays;

/**
 * @author Zhanghaowei
 * @date 2024/05/06 18:38
 */
public enum RechargeType {


    WALLET("1", "钱包充值"),


    WALLET_ACTIVITY("2", "钱包活动充值"),


    FIRST_CHARGE("3", "首冲"),
    MONTH_CARD("7", "购买月卡"),
    MEMBER("6", "会员套餐"),
    GROUP_BUY("8", "拼单"),
    CHARGE_PREPAID("9", "预付后充"),
    ;

    String code;

    String desc;

    RechargeType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static RechargeType getByCode(String code) {
        RechargeType rechargeType0 = Arrays.stream(values()).filter(i -> i.getCode().equals(code)).findFirst().orElse(null);
        Assert.notNull(rechargeType0, "不支持此充值类型");
        return rechargeType0;
    }
}
