package com.chargehub.common.security.template.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * @author Zhanghaowei
 * @date 2024/04/09 16:30
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CrudId {
}
