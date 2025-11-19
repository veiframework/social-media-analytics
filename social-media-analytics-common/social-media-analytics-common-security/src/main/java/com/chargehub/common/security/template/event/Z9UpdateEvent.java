package com.chargehub.common.security.template.event;

import com.chargehub.common.security.template.domain.Z9CrudEntity;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhanghaowei
 * @date 2024/04/03 15:49
 */
@Setter
@Getter
public class Z9UpdateEvent<T extends Z9CrudEntity> implements ResolvableTypeProvider {

    private List<T> data;

    private final Class<T> tClass;

    private final List<Z9CrudDto<T>> dtoDatas;


    public Z9UpdateEvent(List<T> data, List<Z9CrudDto<T>> dtoData, Class<T> tClass) {
        this.data = data;
        this.tClass = tClass;
        this.dtoDatas = dtoData;
    }

    public Z9UpdateEvent(T data, Z9CrudDto<T> dtoData, Class<T> tClass) {
        this.tClass = tClass;
        this.data = Stream.of(data).collect(Collectors.toList());
        this.dtoDatas = Stream.of(dtoData).collect(Collectors.toList());
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forClass(tClass));
    }
}
