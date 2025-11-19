package com.chargehub.common.security.template.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/04/03 17:59
 */
@Setter
@Getter
public class Z9BeforeDeleteEvent<T> implements ResolvableTypeProvider {

    private List<String> data;

    private final Class<T> tClass;


    public Z9BeforeDeleteEvent(List<String> data, Class<T> tClass) {
        this.tClass = tClass;
        this.data = data;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forClass(tClass));
    }
}
