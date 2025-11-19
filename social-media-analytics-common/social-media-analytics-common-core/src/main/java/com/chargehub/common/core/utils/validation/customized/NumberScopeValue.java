package com.chargehub.common.core.utils.validation.customized;

import com.chargehub.common.core.utils.validation.impl.NumberScopeValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {NumberScopeValueValidator.class})
public @interface NumberScopeValue {

    //默认错误消息
    String message() default "数值必须在制定的范围内";

    double minValue();
    double maxValue();

    //分组
    Class<?>[] groups() default {};

    //负载
    Class<? extends Payload>[] payload() default {};

    //指定多个时使用
    @Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        NumberScopeValue[] value();
    }

}
