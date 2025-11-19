package com.chargehub.common.datascope.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 13:36
 */
@Schema
@Data
public class Z9PurviewVo implements Serializable {


    private static final long serialVersionUID = 1815075816994465329L;


    @NotBlank
    @Schema(description = "数据权限id")
    private String purviewId;

    @Schema(description = "层级")
    private Integer treeLevel;


}
