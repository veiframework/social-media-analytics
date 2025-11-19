package com.chargehub.common.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 函数防抖动
 *
 * @author Zhanghaowei
 * @date 2024/05/24 11:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Debounce {

    /**
     * 持续时间毫秒
     *
     * @return
     */
    int duration() default 1500;


}
