package com.chargehub.biz.region.domain.vo;

import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2021-11-05 14:42:56
 */
@Data
@Schema
public class RegionVo implements Serializable, Z9CrudVo {


    private static final long serialVersionUID = -1400470545487520721L;


    @Schema(description = "主键")
    private String id;

    @Dict(dictTable = "charge_region", nameColumn = "label", valueColumn = "id")
    @Schema(description = "父级主键")
    private String parentId;

    @Schema(description = "区域编码")
    private String areaCode;

    @Schema(description = "租户id")
    private String tenantId;

    @Schema(description = "标签名")
    private String label;

    @Schema(description = "备注")
    private String remark;


    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "是否为最后一级")
    private Boolean lastStage;

    @Schema(description = "子集")
    private List<RegionVo> children;

    @Schema(description = "是否可见")
    private String visible;

    @Schema(hidden = true)
    private List<String> idPath;

    @Schema(description = "停止充电soc")
    private Integer stopSoc;

}
