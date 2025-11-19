package com.chargehub.common.security.template.annotation;


import com.chargehub.common.security.template.enums.Z9QueryTypeEnum;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * @author Zhanghaowei
 * @date 2024/04/09 09:23
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CrudQueryField {

    Z9QueryTypeEnum queryType() default Z9QueryTypeEnum.EQ;

}
