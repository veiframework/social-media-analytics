package com.chargehub.common.security.template.enums;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2024/07/18 18:27
 */
public enum DataStatisticTypeEnum {

    DAY("indexDay", new String[]{"日期(周)", "日期(月)", "日期(年)"}, DateField.DAY_OF_YEAR, "yyyy-MM-dd"),

    WEEK("indexWeek", new String[]{"日期", "日期(月)", "日期(年)"}, DateField.WEEK_OF_YEAR, "yyyy-WW"),

    MONTH("indexMonth", new String[]{"日期", "日期(周)", "日期(年)"}, DateField.MONTH, "yyyy-MM"),

    YEAR("indexYear", new String[]{"日期", "日期(周)", "日期(月)"}, DateField.YEAR, "yyyy"),

    HOUR("indexHour", new String[]{}, DateField.HOUR_OF_DAY, "HH:00"),

    CUSTOM("indexDay", new String[]{"日期(周)", "日期(月)", "日期(年)"}, DateField.DAY_OF_YEAR, "yyyy-MM-dd");

    String fieldName;
    String[] exclusion;
    DateField dateField;
    String pattern;


    DataStatisticTypeEnum(String fieldName, String[] exclusion, DateField dateField, String pattern) {
        this.fieldName = fieldName;
        this.exclusion = exclusion;
        this.dateField = dateField;
        this.pattern = pattern;
    }

    public static DataStatisticTypeEnum of(Integer type) {
        Assert.notNull(type, "日期类型不得为空");
        DataStatisticTypeEnum dataStatisticTypeEnum = Arrays.stream(values()).filter(i -> type.equals(i.ordinal())).findFirst().orElse(null);
        Assert.notNull(dataStatisticTypeEnum, "不支持的日期类型");
        return dataStatisticTypeEnum;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String[] getExclusion() {
        return exclusion;
    }

    public DateField getDateField() {
        return dateField;
    }

    public String getPattern() {
        return pattern;
    }

    public static <T> List<T> init0(String startTime,
                                    String endTime,
                                    DataStatisticTypeEnum dataStatisticTypeEnum,
                                    List<T> originData,
                                    Function<Date, T> initMethod,
                                    Function<T, String> timeField) {
        DateField dateField = dataStatisticTypeEnum.getDateField();
        String pattern = dataStatisticTypeEnum.getPattern();
        DateTime start = DateUtil.parse(startTime, pattern);
        DateTime end = DateUtil.parse(endTime, pattern);
        Map<String, T> collect = originData.stream().collect(Collectors.toMap(timeField, v -> v));
        List<T> result = new ArrayList<>();
        for (; start.compareTo(end) <= 0; start.offset(dateField, 1)) {
            String formatStart = start.toString(pattern);
            T t = collect.getOrDefault(formatStart, initMethod.apply(start));
            result.add(t);
        }
        return result;
    }

    public static <T> List<T> init(String startTime,
                                   String endTime,
                                   DataStatisticTypeEnum dataStatisticTypeEnum,
                                   List<T> originData,
                                   Function<Date, T> initMethod,
                                   Function<T, Date> timeField) {
        return init0(startTime, endTime, dataStatisticTypeEnum, originData, initMethod, obj -> {
            Date date = timeField.apply(obj);
            return DateUtil.format(date, dataStatisticTypeEnum.getPattern());
        });
    }


    public static <T> void calculateRingRatio(boolean removeFirst,
                                              List<T> originData,
                                              Map<Function<T, Number>, BiConsumer<T, BigDecimal>> fieldMap) {
        if (CollectionUtils.isEmpty(originData)) {
            return;
        }
        for (int i = 0; i < originData.size(); i++) {
            T data = originData.get(i);
            if (i == 0) {
                fieldMap.forEach((k, v) -> v.accept(data, BigDecimal.ZERO));
                continue;
            }
            T prevData = originData.get(i - 1);
            fieldMap.forEach((k, v) -> {
                Number prevFieldVal = k.apply(prevData);
                Number currentFieldVal = Optional.ofNullable(k.apply(data)).orElse(0);
                if (prevFieldVal != null && prevFieldVal.intValue() != 0) {
                    BigDecimal sub = NumberUtil.sub(currentFieldVal, prevFieldVal);
                    BigDecimal ratio = NumberUtil.div(sub, prevFieldVal).multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP);
                    v.accept(data, ratio);
                    return;
                }
                if (currentFieldVal.intValue() == 0) {
                    v.accept(data, BigDecimal.ZERO);
                    return;
                }
                v.accept(data, BigDecimal.valueOf(100));
            });
        }
        if (!removeFirst) {
            return;
        }
        //环比增长计算时,起始节点需要往前推一个点是不在查询范围内的,所以可以删除
        originData.remove(0);
    }

}
