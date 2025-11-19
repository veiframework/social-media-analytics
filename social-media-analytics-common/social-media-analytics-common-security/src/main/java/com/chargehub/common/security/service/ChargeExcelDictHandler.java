package com.chargehub.common.security.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.mapper.ChargeSysDictData;
import com.chargehub.common.security.mapper.ChargeSysDictDataMapper;
import com.chargehub.common.security.mapper.DynamicDictMapper;
import com.chargehub.common.security.template.service.Z9CrudService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2024/04/02 11:45
 */
@Service
public class ChargeExcelDictHandler implements IExcelDictHandler, Dict.DictionarySerializerProcessor {

    private static final int TTL = 3000;

    private final TimedCache<String, String> timedCache = new TimedCache<>(TTL);

    @Autowired
    private ChargeSysDictDataMapper chargeSysDictDataMapper;


    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    private DynamicDictMapper dynamicDictMapper;

    @SuppressWarnings("all")
    @Override
    public List<Map> getList(String dictType) {
        String[] split = dictType.split(StringPool.COMMA);
        if (split.length == 3) {
            String dictTable = split[0];
            String labelNameKey = split[1];
            String sql = "SELECT " + labelNameKey + " FROM " + dictTable;
            List<Map<String, Object>> listMaps = dynamicDictMapper.listMaps(sql);
            return collectListMap(listMaps, labelNameKey);
        }
        if (split.length == 4) {
            String labelNameKey = split[1];
            String beanName = split[3];
            Object bean = applicationContext.getBean(beanName);
            if (bean instanceof IService) {
                IService iService = (IService) bean;
                List<Map<String, Object>> listMaps = iService.listMaps();
                return collectListMap(listMaps, labelNameKey);
            }
            if (bean instanceof Z9CrudService) {
                Z9CrudService<?> iService = (Z9CrudService<?>) bean;
                List<Map<String, Object>> listMaps = iService.listMaps();
                return collectListMap(listMaps, labelNameKey);
            }
            throw new IllegalArgumentException("不支持该serivce的实现");
        }
        String dictType0 = split[0];
        List<ChargeSysDictData> dictListByType = chargeSysDictDataMapper.getDictListByType(dictType);
        return collectListMap(dictListByType);
    }

    @Override
    public String toName(String dictType, Object o, String name, Object value) {
        String cache = timedCache.get(dictType + value);
        if (StringUtils.isNotBlank(cache)) {
            return cache;
        }
        String[] split = dictType.split(StringPool.COMMA);
        if (split.length > 1) {
            String dictTable = split[0];
            String nameColumn = split[1];
            String valueColumn = split[2];
            String sql = "SELECT " + nameColumn + " FROM " + dictTable
                    + " WHERE " + valueColumn + " = " + "'" + value + "'";
            if (split.length > 4) {
                String parseVal = dictType.split(",", 4)[3];
                sql = parseVal.replace("#{value}", "'" + value + "'");
            }
            Map<String, Object> stringObjectMap = dynamicDictMapper.applySql(sql);
            if (MapUtil.isEmpty(stringObjectMap)) {
                return String.valueOf(value);
            }
            String dictName = String.valueOf(stringObjectMap.get(nameColumn));
            timedCache.put(dictType + value, dictName, TTL);
            return dictName;
        }
        String dictType0 = split[0];
        String dictName = chargeSysDictDataMapper.getDictName(dictType0, value);
        timedCache.put(dictType + value, dictName, TTL);
        return dictName;
    }

    @Override
    public String toValue(String dictType, Object o, String name, Object value) {
        String cache = timedCache.get(dictType + value);
        if (StringUtils.isNotBlank(cache)) {
            return cache;
        }
        String[] split = dictType.split(StringPool.COMMA);
        if (split.length > 1) {
            String dictTable = split[0];
            String nameColumn = split[1];
            String valueColumn = split[2];
            String sql = "SELECT " + valueColumn + " FROM " + dictTable
                    + " WHERE " + nameColumn + " = " + "'" + value + "'";
            if (split.length > 4) {
                String parseVal = dictType.split(",", 4)[3];
                sql = parseVal.replace("#{value}", "'" + value + "'");
            }
            Map<String, Object> stringObjectMap = dynamicDictMapper.applySql(sql);
            if (MapUtil.isEmpty(stringObjectMap)) {
                return String.valueOf(value);
            }
            String dictValue = String.valueOf(stringObjectMap.get(valueColumn));
            timedCache.put(dictType + value, dictValue, TTL);
            return dictValue;
        }
        String dictType0 = split[0];
        String dictValue = chargeSysDictDataMapper.getDictValue(dictType0, String.valueOf(value));
        timedCache.put(dictType + value, dictValue, TTL);
        return dictValue;
    }


    @SuppressWarnings("all")
    public List<Map> collectListMap(List<Map<String, Object>> dictInfoList, String labelNameKey) {
        if (CollectionUtils.isEmpty(dictInfoList)) {
            return Lists.newArrayList(MapUtil.of("dictValue", ""));
        }
        return dictInfoList.stream().map(i -> {
            Map<String, Object> map = MapUtil.builder(new HashMap<String, Object>(16))
                    .build();
            Object value = i.get(labelNameKey);
            if (value == null) {
                value = i.get(labelNameKey.toUpperCase());
            }
            map.put("dictValue", value);
            return map;
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("all")
    public List<Map> collectListMap(List<ChargeSysDictData> dictInfoList) {
        if (CollectionUtils.isEmpty(dictInfoList)) {
            return Lists.newArrayList(MapUtil.of("dictValue", ""));
        }
        return dictInfoList.stream().map(i -> MapUtil.builder(new HashMap<String, Object>(16))
                .put("dictValue", i.getDictLabel())
                .build()).collect(Collectors.toList());
    }

    @Override
    public void serialize(Dict dict, String value, String fieldName, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value);
        String dictTable = StringUtils.isNotBlank(dict.dictTable()) ? dict.dictTable() : dict.dictType();
        String nameColumn = dict.nameColumn();
        String valueColumn = dict.valueColumn();
        String sql = dict.sql();
        String join = String.join(StringPool.COMMA, dictTable, nameColumn, valueColumn);
        if (StringUtils.isNotBlank(sql)) {
            join = join + StringPool.COMMA + sql;
        }
        String dictName = this.toName(join, null, null, value);
        if (StringUtils.isBlank(dictName)) {
            return;
        }
        String dictNameSuffix = dict.dictNameSuffix();
        String dictFieldName = fieldName + dictNameSuffix;
        gen.writeStringField(dictFieldName, dictName);
    }


}
