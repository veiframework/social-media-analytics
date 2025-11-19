package com.chargehub.biz.region.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/05/15 09:58
 */
@Data
@Schema
public class RegionAppQueryDto implements Serializable {
    private static final long serialVersionUID = -2601485980228769009L;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "纬度")
    private String latitude;

    private Integer level;

    private String parentId;


}
