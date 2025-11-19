package com.chargehub.common.security.template.mybatis;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.chargehub.common.security.template.annotation.CrudId;
import com.chargehub.common.security.template.annotation.CrudPurviewField;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.annotation.CrudSubUniqueId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import com.chargehub.common.security.template.domain.Z9CrudQueryCondition;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;
import com.chargehub.common.security.template.event.PurviewQueryEvent;
import com.chargehub.common.security.template.mapper.Z9BaseCrud;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zhanghaowei
 * @since 2023-12-13 15:59
 */
public interface Z9MpCrudMapper<E extends Z9CrudEntity> extends BaseMapper<E>, Z9BaseCrud<E> {


    /**
     * 新增
     *
     * @param t
     */
    @Override
    default void doCreate(E t) {
        this.insert(t);
    }

    /**
     * 修改
     *
     * @param t
     * @param id
     */
    @Override
    default void doEdit(E t, String id) {
        t.setUniqueId(id);
        this.updateById(t);
    }

    /**
     * 获取详情
     *
     * @param id
     * @return
     */
    @Override
    default E doGetDetailById(String id) {
        return this.selectById(id);
    }

    /**
     * 批量删除
     *
     * @param idList
     */
    @Override
    default void doDeleteByIds(Collection<?> idList) {
        this.deleteBatchIds(idList);
    }

    /**
     * 查全部
     *
     * @param queryDto
     * @return
     */
    @Override
    default List<E> doGetAll(Z9CrudQueryDto<E> queryDto) {
        return this.selectList(this.getQw(queryDto));
    }

    /**
     * 是否存在
     *
     * @param entity
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    default E doGetExist(E entity, String id) {
        Class<E> eClass = (Class<E>) entity.getClass();
        Field[] fields = ReflectUtil.getFields(entity.getClass());
        List<Field> subUniqueIdFields = Arrays.stream(fields).filter(i -> i.isAnnotationPresent(CrudSubUniqueId.class)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(subUniqueIdFields)) {
            return null;
        }
        LambdaQueryChainWrapper<E> qw = new LambdaQueryChainWrapper<>(eClass);
        subUniqueIdFields.forEach(field -> {
            Object value = ReflectUtil.getFieldValue(entity, field);
            String fieldName = field.getName();
            qw.eq(new Z9MpDynamicLambda<>(fieldName, eClass), value);
        });
        Field idField = Arrays.stream(fields).filter(i -> i.isAnnotationPresent(TableId.class) || i.isAnnotationPresent(CrudId.class))
                .findFirst().orElse(null);
        Assert.notNull(idField, "未找到主键");
        qw.ne(StringUtils.isNotBlank(id), new Z9MpDynamicLambda<>(idField.getName(), eClass), id);
        return qw.one();
    }

    /**
     * 分页
     *
     * @param queryDto
     * @return
     */
    @Override
    default IPage<E> doGetPage(Z9CrudQueryDto<E> queryDto) {
        return this.selectPage(queryDto.buildPageObj(), this.getQw(queryDto));
    }


    /**
     * 批量插入
     *
     * @param list
     */
    @Override
    default void doSaveExcelData(List<E> list) {
        Db.saveBatch(list);
    }

    /**
     * 获取集合列表
     *
     * @return
     */
    @Override
    default List<Map<String, Object>> doListMaps() {
        return this.selectMaps(this.getQw(null));
    }

    /**
     * 构建查询条件
     *
     * @param queryDto
     * @return
     */
    default LambdaQueryWrapper<E> getQw(Z9CrudQueryDto<E> queryDto) {
        Class<E> eClass = this.getEntity();
        LambdaQueryWrapper<E> qw = Wrappers.lambdaQuery(eClass);
        buildQuery(qw, queryDto, eClass);
        buildPurview(qw, queryDto, eClass);
        buildOrder(qw, queryDto, eClass);
        return qw;
    }


    default void buildQuery(LambdaQueryWrapper<E> qw, Z9CrudQueryDto<E> queryDto, Class<E> eClass) {
        if (queryDto == null) {
            return;
        }
        Field[] fields = ReflectUtil.getFields(queryDto.getClass());
        if (ArrayUtils.isEmpty(fields)) {
            return;
        }
        List<Z9CrudQueryCondition<E>> list = new ArrayList<>();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(CrudQueryField.class)) {
                continue;
            }
            list.add(new Z9CrudQueryCondition<>(queryDto, field, eClass));
        }
        List<Z9CrudQueryCondition<E>> queryConditions = queryDto.queryConditions();
        if (CollectionUtils.isNotEmpty(queryConditions)) {
            list.addAll(queryConditions);
        }
        list.forEach(queryCondition -> buildQueryCondition(qw, queryCondition));
    }


    default void buildQueryCondition(LambdaQueryWrapper<E> qw, Z9CrudQueryCondition<E> i) {
        Object value = i.getValue();
        if (ObjectUtil.isEmpty(value)) {
            return;
        }
        if (i.getQueryType() == Z9QueryTypeEnum.EQ) {
            qw.eq(i.getField(), value);
        }
        if (i.getQueryType() == Z9QueryTypeEnum.NE) {
            qw.ne(i.getField(), value);
        }
        if (i.getQueryType() == Z9QueryTypeEnum.LIKE) {
            qw.like(i.getField(), value);
        }
        if (i.getQueryType() == Z9QueryTypeEnum.IN) {
            Collection<?> collection = (Collection<?>) value;
            qw.in(i.getField(), new ArrayList<>(collection));
        }
        if (i.getQueryType() == Z9QueryTypeEnum.BETWEEN) {
            if (value instanceof Iterable) {
                Collection<?> collection = (Collection<?>) value;
                List<?> list = new ArrayList<>(collection);
                qw.between(i.getField(), list.get(0), list.get(1));
            } else if (value.getClass().isArray()) {
                Object[] objects = (Object[]) value;
                qw.between(i.getField(), objects[0], objects[1]);
            } else {
                throw new IllegalArgumentException("between查询字段类型必须是集合");
            }
        }
        if (i.getQueryType() == Z9QueryTypeEnum.IN_SQL) {
            qw.inSql(i.getField(), String.valueOf(value));
        }
        if (i.getQueryType() == Z9QueryTypeEnum.LT) {
            qw.lt(i.getField(), value);
        }
        if (i.getQueryType() == Z9QueryTypeEnum.GT) {
            qw.gt(i.getField(), value);
        }
        if (i.getQueryType() == Z9QueryTypeEnum.LE) {
            qw.le(i.getField(), value);
        }
        if (i.getQueryType() == Z9QueryTypeEnum.GE) {
            qw.ge(i.getField(), value);
        }
    }

    default void buildOrder(LambdaQueryWrapper<E> qw, Z9CrudQueryDto<E> queryDto, Class<E> eClass) {
        if (qw == null) {
            return;
        }
        if (queryDto == null) {
            return;
        }
        Set<String> ascFields = queryDto.getAscFields();
        Set<String> descFields = queryDto.getDescFields();
        if (CollectionUtils.isEmpty(ascFields) && CollectionUtils.isEmpty(descFields) && queryDto.autoOrder()) {
            Field idField = Arrays.stream(ReflectUtil.getFields(eClass)).filter(i -> i.isAnnotationPresent(TableId.class) || i.isAnnotationPresent(CrudId.class))
                    .findFirst().orElse(null);
            if (idField == null) {
                return;
            }
            qw.orderByDesc(new Z9MpDynamicLambda<>(idField.getName(), eClass));
            return;
        }
        if (CollectionUtils.isNotEmpty(ascFields)) {
            for (String ascField : ascFields) {
                qw.orderByAsc(new Z9MpDynamicLambda<>(ascField, eClass));
            }
        }
        if (CollectionUtils.isNotEmpty(descFields)) {
            for (String descField : descFields) {
                qw.orderByDesc(new Z9MpDynamicLambda<>(descField, eClass));
            }
        }
    }


    default void buildPurview(LambdaQueryWrapper<E> qw, Z9CrudQueryDto<E> queryDto, Class<E> eClass) {
        List<Z9CrudQueryCondition<E>> purviewConditions = this.buildPurviewCondition(eClass);
        if (CollectionUtils.isEmpty(purviewConditions)) {
            return;
        }
        if (queryDto != null && queryDto.isDisablePurview()) {
            return;
        }
        SpringUtil.publishEvent(new PurviewQueryEvent<>(purviewConditions, qw));
    }

    default List<Z9CrudQueryCondition<E>> buildPurviewCondition(Class<E> eClass) {
        Field[] entityFields = ReflectUtil.getFields(eClass);
        if (ArrayUtils.isEmpty(entityFields)) {
            return new ArrayList<>();
        }
        return Arrays.stream(entityFields).filter(field -> field.isAnnotationPresent(CrudPurviewField.class)).map(field -> {
            Z9CrudQueryCondition<E> purviewCondition = new Z9CrudQueryCondition<>(field, eClass);
            Object value = purviewCondition.getValue();
            if (ObjectUtil.isEmpty(value)) {
                return null;
            }
            return purviewCondition;
        }).filter(Objects::nonNull).sorted(Comparator.comparing(Z9CrudQueryCondition::isPurviewField)).collect(Collectors.toList());
    }

    /**
     * 查询
     *
     * @return
     */
    default LambdaQueryChainWrapper<E> lambdaQuery() {
        return new LambdaQueryChainWrapper<>(this);
    }

    /**
     * 更新
     *
     * @return
     */
    default LambdaUpdateChainWrapper<E> lambdaUpdate() {
        return new LambdaUpdateChainWrapper<>(this);
    }


}
