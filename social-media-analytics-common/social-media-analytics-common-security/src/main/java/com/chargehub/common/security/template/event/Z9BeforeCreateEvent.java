package com.chargehub.common.security.template.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhanghaowei
 * @date 2024/04/03 17:58
 */
@Setter
@Getter
public class Z9BeforeCreateEvent<T> implements ResolvableTypeProvider {

    private List<T> data;


    private final Class<T> tClass;

    public Z9BeforeCreateEvent(List<T> data, Class<T> tClass) {
        this.data = data;
        this.tClass = tClass;
    }

    public Z9BeforeCreateEvent(T data, Class<T> tClass) {
        this.tClass = tClass;
        this.data = Stream.of(data).collect(Collectors.toList());
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forClass(tClass));
    }
}
