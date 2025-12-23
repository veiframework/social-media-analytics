package com.chargehub.common.security.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

/**
 * 捕获异常的序列化异常
 *
 * @author Zhanghaowei
 * @date 2021/12/22 8:47
 *
 * <p>
 * mapper.addHandler(new DeserializationProblemHandler() {
 * public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType, JsonToken t, JsonParser p, String failureMsg) throws IOException {
 * if(targetType.isTypeOrSubTypeOf(String.class)){
 * <p>
 * }
 * return super.handleUnexpectedToken(ctxt, targetType, t, p, failureMsg);
 * });
 * </p>
 */
public final class JacksonUtil {

    private JacksonUtil() {
    }

    public static final DateTimeFormatter Y_M_D = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter YMD_HM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

    public static final DateTimeFormatter HMS = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final DateTimeFormatter NO_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final ObjectMapper MAPPER = getMapper();


    public static ObjectMapper getMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(NON_NULL);
        objectMapper.setSerializationInclusion(NON_EMPTY);
        //控制是否序列化public或private的字段: setVisibility
        JavaTimeModule javaTimeModule = getTimeModule();
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    public static ObjectMapper getTypeMapper() {
        ObjectMapper objectMapper = getMapper();
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }


    public static JavaTimeModule getTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //HH:mm:ss
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(HMS));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(HMS));
        //yyyy-MM-dd
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(YMD));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(YMD));
        //yyyy-MM-dd HH:mm:ss
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(Y_M_D));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Y_M_D));
        //yyyy-MM
        javaTimeModule.addSerializer(YearMonth.class, new YearMonthSerializer(DatePattern.NORM_MONTH_FORMATTER));
        javaTimeModule.addDeserializer(YearMonth.class, new YearMonthDeserializer(DatePattern.NORM_MONTH_FORMATTER));
        //date类型
        javaTimeModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN)));
        javaTimeModule.addDeserializer(Date.class, new DateDeserializers.DateDeserializer(new DateDeserializers.DateDeserializer(), new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN), DatePattern.NORM_DATETIME_PATTERN));
        return javaTimeModule;
    }


    /**
     * Object to json string.
     *
     * @param obj obj
     * @return json string
     * @throws IllegalArgumentException if transfer failed
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Object to json string byte array.
     *
     * @param obj obj
     * @return json string byte array
     * @throws IllegalArgumentException if transfer failed
     */
    public static byte[] toJsonBytes(Object obj) {
        try {
            if (obj == null) {
                return new byte[0];
            }
            return MAPPER.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  class of object
     * @param <T>  General type
     * @return object
     * @throws IllegalArgumentException if deserialize failed
     */
    public static <T> T toObj(byte[] json, Class<T> cls) {
        try {
            String jsonStr = new String(json, StandardCharsets.UTF_8);
            return toObj(jsonStr, cls);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  {@link Type} of object
     * @param <T>  General type
     * @return object
     * @throws IllegalArgumentException if deserialize failed
     */
    public static <T> T toObj(byte[] json, Type cls) {
        try {
            String jsonStr = new String(json, StandardCharsets.UTF_8);
            return toObj(jsonStr, cls);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param inputStream json string input stream
     * @param cls         class of object
     * @param <T>         General type
     * @return object
     * @throws IllegalArgumentException if deserialize failed
     */
    public static <T> T toObj(InputStream inputStream, Class<T> cls) {
        try {
            return MAPPER.readValue(inputStream, cls);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json          json string byte array
     * @param typeReference {@link TypeReference} of object
     * @param <T>           General type
     * @return object
     * @throws IllegalArgumentException if deserialize failed
     */
    public static <T> T toObj(byte[] json, TypeReference<T> typeReference) {
        try {
            String jsonStr = new String(json, StandardCharsets.UTF_8);
            return toObj(jsonStr, typeReference);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param cls  class of object
     * @param <T>  General type
     * @return object
     * @throws IllegalArgumentException if deserialize failed
     */
    public static <T> T toObj(String json, Class<T> cls) {
        try {
            return MAPPER.readValue(json, cls);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json json string
     * @param type {@link Type} of object
     * @param <T>  General type
     * @return object
     * @throws IllegalArgumentException if deserialize failed
     */
    public static <T> T toObj(String json, Type type) {
        try {
            return MAPPER.readValue(json, MAPPER.constructType(type));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param json          json string
     * @param typeReference {@link TypeReference} of object
     * @param <T>           General type
     * @return object
     * @throws IllegalArgumentException if deserialize failed
     */
    public static <T> T toObj(String json, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param inputStream json string input stream
     * @param type        {@link Type} of object
     * @param <T>         General type
     * @return object
     * @throws IllegalArgumentException if deserialize failed
     */
    public static <T> T toObj(InputStream inputStream, Type type) {
        try {
            return MAPPER.readValue(inputStream, MAPPER.constructType(type));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Json string deserialize to Jackson {@link JsonNode}.
     *
     * @param json json string
     * @return {@link JsonNode}
     * @throws IllegalArgumentException if deserialize failed
     */
    public static JsonNode toObj(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> Object toObject(String jsonStr, Class<T> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonStr);
            if (jsonNode.isArray()) {
                JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
                return MAPPER.convertValue(jsonNode, javaType);
            }
            return MAPPER.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Register sub type for child class.
     *
     * @param clz  child class
     * @param type type name of child class
     */
    public static void registerSubtype(Class<?> clz, String type) {
        MAPPER.registerSubtypes(new NamedType(clz, type));
    }

    /**
     * Create a new empty Jackson {@link ObjectNode}.
     *
     * @return {@link ObjectNode}
     */
    public static ObjectNode createEmptyJsonNode() {
        return new ObjectNode(MAPPER.getNodeFactory());
    }

    /**
     * Create a new empty Jackson {@link ArrayNode}.
     *
     * @return {@link ArrayNode}
     */
    public static ArrayNode createEmptyArrayNode() {
        return new ArrayNode(MAPPER.getNodeFactory());
    }

    /**
     * Parse object to Jackson {@link JsonNode}.
     *
     * @param obj object
     * @return {@link JsonNode}
     */
    public static JsonNode transferToJsonNode(Object obj) {
        return MAPPER.valueToTree(obj);
    }

    /**
     * construct java type -> Jackson Java Type.
     *
     * @param type java type
     * @return JavaType {@link JavaType}
     */
    public static JavaType constructJavaType(Type type) {
        return MAPPER.constructType(type);
    }


    public static JsonNode transferToJsonNode(Object o, String jsonPointerPath, Consumer<ObjectNode> nodeConsumer) {
        JsonNode jsonNode = JacksonUtil.transferToJsonNode(o);

        if (CharSequenceUtil.isNotBlank(jsonPointerPath)) {
            JsonPointer jsonPointer = JsonPointer.valueOf(jsonPointerPath);
            JsonNode pointJsonNode = jsonNode.at(jsonPointer);
            recursiveNode(pointJsonNode, nodeConsumer);
        } else {
            recursiveNode(jsonNode, nodeConsumer);
        }
        return jsonNode;
    }


    public static void recursiveNode(JsonNode jsonNode, Consumer<ObjectNode> nodeConsumer) {
        boolean plainObj = Stream.of(jsonNode).noneMatch(i -> i.isObject() && i.isArray());
        if (plainObj && jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            nodeConsumer.accept(objectNode);
            return;
        }
        for (JsonNode node : jsonNode) {
            if (node.isArray()) {
                recursiveNode(node, nodeConsumer);
            }
            if (node.isObject()) {
                ObjectNode objectNode = (ObjectNode) node;
                nodeConsumer.accept(objectNode);
            }
        }
    }

    public static JsonNode readJsonNode(JsonNode jsonNode, String field) {
        Assert.notBlank(field, "read json node field can not be null");
        if (jsonNode == null) {
            return MissingNode.getInstance();
        }
        return (jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance());
    }

    public static <T, V> TypeReference<Map<T, V>> getMapReference() {
        return new TypeReference<Map<T, V>>() {
        };
    }

    public static <T> T readField(byte[] body, String fieldName, Class<T> clazz) {
        JsonFactory factory = MAPPER.getFactory();
        try (JsonParser parser = factory.createParser(body)) {
            while (parser.nextToken() != null) {
                if (parser.getCurrentToken() != JsonToken.FIELD_NAME) {
                    continue;
                }
                String currentName = parser.getCurrentName();
                if (fieldName.equals(currentName)) {
                    parser.nextToken();
                    return parser.readValueAs(clazz);
                }
            }
            return null;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
