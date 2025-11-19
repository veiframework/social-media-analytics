package com.chargehub.common.datascope.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springdoc.api.annotations.ParameterObject;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 14:20
 */
@Data
@ParameterObject
@Schema
public class Z9PurviewTreeQueryVo implements Serializable {

    private static final long serialVersionUID = -5323071017269502826L;

    @Schema(description = "权限类型")
    private Set<String> purviewTypes;

}
