package com.chargehub.common.datascope.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 13:37
 */
@Data
@Schema
public class Z9PurviewCreateOrUpdateVo implements Serializable {
    private static final long serialVersionUID = -3281011520400996157L;

    @NotBlank
    @Schema(description = "权限类型")
    private String purviewType;

    @NotBlank
    @Schema(description = "数据资源id")
    private String ownerId;

    @Schema(description = "权限id")
    private List<Z9PurviewVo> purviewList;

    @Schema(description = "是否全部数据权限")
    private boolean all;
}
