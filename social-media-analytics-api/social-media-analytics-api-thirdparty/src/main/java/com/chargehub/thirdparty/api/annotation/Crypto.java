package com.chargehub.thirdparty.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zhanghaowei
 * @date 2024/08/05 13:40
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Crypto {

    /**
     * bean名称
     *
     * @return
     */
    String name();

}
