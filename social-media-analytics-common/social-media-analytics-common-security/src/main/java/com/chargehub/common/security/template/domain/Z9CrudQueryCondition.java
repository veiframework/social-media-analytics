package com.chargehub.common.security.template.domain;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.chargehub.common.security.template.annotation.CrudPurviewField;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import com.chargehub.common.security.template.mybatis.Z9MpDynamicLambda;
import com.chargehub.common.security.utils.SecurityUtils;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @date 2024/04/04 17:52
 */
@Data
public class Z9CrudQueryCondition<E extends Z9CrudEntity> {

    private SFunction<E, Object> field;

    private boolean purviewField;

    private List<String> purviewTypes;

    private Z9QueryTypeEnum queryType;

    private Object value;

    public Z9CrudQueryCondition() {
    }

    public Z9CrudQueryCondition(Z9CrudQueryDto<E> queryDto, Field field, Class<E> eClass) {
        String fieldName = field.getName();
        CrudQueryField annotation = field.getAnnotation(CrudQueryField.class);
        this.queryType = annotation.queryType();
        this.field = new Z9MpDynamicLambda<>(fieldName, eClass);
        this.value = ReflectUtil.getFieldValue(queryDto, field);
    }

    public Z9CrudQueryCondition(Field field, Class<E> eClass) {
        String fieldName = field.getName();
        CrudPurviewField annotation = field.getAnnotation(CrudPurviewField.class);
        List<String> purviewTypeList = Arrays.stream(annotation.purviewTypes()).collect(Collectors.toList());
        this.queryType = annotation.queryType();
        this.field = new Z9MpDynamicLambda<>(fieldName, eClass);
        this.value = this.purviewSql(purviewTypeList);
        this.purviewField = true;
        this.purviewTypes = purviewTypeList;
    }

    public Z9CrudQueryCondition(SFunction<E, Object> field, Z9QueryTypeEnum queryType, Object value) {
        this.field = field;
        this.queryType = queryType;
        this.value = value;
    }


    public Z9CrudQueryCondition(SFunction<E, Object> field, List<String> purviewTypes, Z9QueryTypeEnum queryType) {
        this.field = field;
        this.purviewTypes = purviewTypes;
        this.queryType = queryType;
        this.purviewField = true;
    }

    /**
     * puviewSql
     *
     * @return
     */
    public String purviewSql(List<String> purviewTypes) {
        return SecurityUtils.getPurviewSql(purviewTypes);
    }
}
