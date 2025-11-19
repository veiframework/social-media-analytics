package com.chargehub.common.security.template.mybatis;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;

/**
 * @author Zhanghaowei
 * @date 2024/04/09 13:33
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Z9MpDynamicLambda<E extends Z9CrudEntity> extends SerializedLambda implements SFunction<E, Object> {

    private static final long serialVersionUID = -959193720369350866L;

    private String methodName;

    private Class<E> eClass;

    public Z9MpDynamicLambda(String fieldName, Class<E> eClass) {
        Method getterMethod = BeanUtil.getBeanDesc(eClass).getGetter(fieldName);
        Assert.notNull(getterMethod, "domain对象上不存在字段" + fieldName);
        this.methodName = getterMethod.getName();
        this.eClass = eClass;
    }

    @Override
    public String getImplMethodName() {
        return this.getMethodName();
    }

    @Override
    public String getInstantiatedMethodType() {
        return "(L" + eClass.getCanonicalName() + ";";
    }

    @Override
    public Class<?> getCapturingClass() {
        return eClass;
    }


    @Override
    public Object apply(E e) {
        return null;
    }
}
