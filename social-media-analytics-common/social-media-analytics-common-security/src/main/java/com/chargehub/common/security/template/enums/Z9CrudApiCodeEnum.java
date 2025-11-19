package com.chargehub.common.security.template.enums;

import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.chargehub.common.security.template.domain.Z9CrudPermission;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/05/27 13:09
 */
@Slf4j
public enum Z9CrudApiCodeEnum {

    CREATE,

    EDIT,

    DELETE,

    PAGE,

    DETAILS,

    LIST,

    EXPORT,

    IMPORT,

    EXCEL_TEMPLATE;

    public static Map<Z9CrudApiCodeEnum, Z9CrudPermission> autoGeneratePermissionId(Class<?> dtoClass, boolean debug) {
        Map<Z9CrudApiCodeEnum, Z9CrudPermission> defaultPermissions = new EnumMap<>(Z9CrudApiCodeEnum.class);
        if (dtoClass == null) {
            return defaultPermissions;
        }
        Class<?>[] typeArguments = GenericTypeUtils.resolveTypeArguments(dtoClass, Z9CrudDto.class);
        String className = typeArguments[0].getName();
        for (Z9CrudApiCodeEnum apiCodeEnum : Z9CrudApiCodeEnum.values()) {
            if (apiCodeEnum == LIST) {
                continue;
            }
            String permissionId = String.join(":", className, "crud", apiCodeEnum.name());
            if (debug) {
                log.debug("自动生成接口权限id: {}", permissionId);
            }
            defaultPermissions.put(apiCodeEnum, new Z9CrudPermission(new String[]{permissionId}));
        }
        return defaultPermissions;
    }

}
