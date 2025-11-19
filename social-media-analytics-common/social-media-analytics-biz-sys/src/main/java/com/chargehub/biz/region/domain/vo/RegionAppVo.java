package com.chargehub.biz.region.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/05/15 09:36
 */
@Data
@Schema
public class RegionAppVo implements Serializable {

    private static final long serialVersionUID = -1510603628028316693L;


    @Schema(description = "当前区域")
    private RegionVo currentRegion;


    @Schema(description = "全部区域")
    private List<RegionVo> all;


}
