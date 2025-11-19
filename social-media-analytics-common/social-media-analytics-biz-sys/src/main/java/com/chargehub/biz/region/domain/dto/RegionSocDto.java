package com.chargehub.biz.region.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/03/05 11:11
 */
@Data
public class RegionSocDto implements Serializable {
    private static final long serialVersionUID = 321423173657906021L;

    @NotBlank
    @Schema(description = "区域id")
    private String regionId;

    @NotNull
    @Schema(description = "停止充电soc")
    private Integer stopSoc;

}
