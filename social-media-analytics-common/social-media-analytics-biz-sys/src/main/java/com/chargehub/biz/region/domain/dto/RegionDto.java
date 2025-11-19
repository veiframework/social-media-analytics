package com.chargehub.biz.region.domain.dto;


import com.chargehub.biz.region.domain.RegionEntity;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/04/01 14:39
 */
@Data
@Schema
public class RegionDto implements Serializable, Z9CrudDto<RegionEntity> {


    private static final long serialVersionUID = 7219157225067105416L;

    @NotBlank
    @Schema(description = "标签名")
    private String label;

    @Schema(description = "区域编码")
    private String areaCode;

    @Schema(description = "父级id")
    private String parentId;

    @NotNull
    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "备注")
    private String remark;

    private Integer rowNum;


    private String errorMsg;

}
