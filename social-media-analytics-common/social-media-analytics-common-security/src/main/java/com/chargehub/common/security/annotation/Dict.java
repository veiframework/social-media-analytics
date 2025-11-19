package com.chargehub.common.security.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.*;

/**
 * @author Zhanghaowei
 * @date 2024/03/25 14:46
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@Target(ElementType.FIELD)
@JsonSerialize(using = Dict.DictionarySerializer.class)
public @interface Dict {

    /**
     * 方法描述:  名列
     *
     * @return 返回类型： String
     */
    String nameColumn() default "";

    /**
     * 方法描述:  值列
     *
     * @return 返回类型： String
     */
    String valueColumn() default "";

    /**
     * 方法描述: 数据字典表
     *
     * @return 返回类型： String
     */
    String dictTable() default "";

    /**
     * 系统字典key
     *
     * @return 返回类型： String
     */
    String dictType() default "";

    /**
     * 字典分组
     *
     * @return
     */
    String dictGroup() default "";

    /**
     * 字典分组列名
     *
     * @return
     */
    String groupColumn() default "";


    /**
     * 字典名称后缀
     *
     * @return
     */
    String dictNameSuffix() default "_dictText";

    /**
     * sql
     *
     * @return
     */
    String sql() default "";

    @Component
    @Slf4j
    public class DictionarySerializer extends JsonSerializer<String> implements ContextualSerializer {

        private Dict dict;

        private static DictionarySerializerProcessor dictionarySerializerProcessor;

        private String fieldName;

        @SuppressWarnings("all")
        public DictionarySerializer(@Autowired(required = false) DictionarySerializerProcessor dictionarySerializerProcessor) {
            DictionarySerializer.dictionarySerializerProcessor = dictionarySerializerProcessor;
        }

        @Override
        public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (dict == null) {
                jsonGenerator.writeString(s);
                return;
            }
            if (dictionarySerializerProcessor == null) {
                log.info(" [Vei] please considering set operator serializer");
                jsonGenerator.writeString(s);
                return;
            }
            if (StringUtils.isBlank(s)) {
                jsonGenerator.writeString(s);
                return;
            }
            dictionarySerializerProcessor.serialize(dict, s, fieldName, jsonGenerator, serializerProvider);
        }

        @Override
        public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
            Dict annotation = beanProperty.getAnnotation(Dict.class);
            if (annotation != null) {
                this.dict = annotation;
                this.fieldName = beanProperty.getName();
            }
            return this;
        }


    }

    public static interface DictionarySerializerProcessor {

        void serialize(Dict dict, String value, String fieldName, JsonGenerator gen, SerializerProvider serializers) throws IOException;

    }
}
