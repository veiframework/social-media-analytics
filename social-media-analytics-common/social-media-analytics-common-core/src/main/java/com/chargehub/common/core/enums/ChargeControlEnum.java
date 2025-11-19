package com.chargehub.common.core.enums;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * 充电控制枚举
 * date: 2023/08
 *
 * @author TiAmo(13721682347 @ 163.com)
 */
public enum ChargeControlEnum {

    BE_FULL("0", "充满", "0", (unit, value) -> value.multiply(new BigDecimal(unit))),

    MONEY("3", "金额", "100", (unit, value) -> value.multiply(new BigDecimal(unit))),

    TIME("2", "时间（分）", "1", (unit, value) -> value.multiply(new BigDecimal(unit))),

    ELECTRIC_QUANTITY("1", "电量（kWh）", "10", (unit, value) -> value.multiply(new BigDecimal(unit))),

    /**
     * 钱包余额单位换算，不计入充电方式
     */
    BALANCE("BALANCE", "余额换", "100", (unit, value) -> value.multiply(new BigDecimal(unit))),
    ;

    private final String code;

    private final String desc;

    private final String unit;

    private final BiFunction<String, BigDecimal, BigDecimal> unitConverter;

    ChargeControlEnum(String code, String desc, String unit, BiFunction<String, BigDecimal, BigDecimal> unitConverter) {
        this.code = code;
        this.desc = desc;
        this.unit = unit;
        this.unitConverter = unitConverter;
    }

    /**
     * 根据code 查询
     *
     * @param code code
     * @return ChargeControlEnum
     */
    public static ChargeControlEnum byCode(String code) {
        Optional<ChargeControlEnum> first = Arrays.stream(values()).filter(i -> StringUtils.equals(code, i.getCode())).findFirst();
        return first.orElse(null);
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getUnit() {
        return unit;
    }

    public BiFunction<String, BigDecimal, BigDecimal> getUnitConverter() {
        return unitConverter;
    }
}
