package com.chargehub.biz.region.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 区域三级联动可见配置,必须上下级一块可见
 *
 * @author Zhanghaowei
 * @date 2024/08/28 16:18
 */
@Data
public class RegionVisibleDto implements Serializable {
    private static final long serialVersionUID = -841986658377748229L;

    @Valid
    @Schema(description = "节点数组")
    private List<RegionNode> nodeList;

    @NotBlank
    @Schema(description = "是否可见0-不可见,1-可见")
    private String visible;


}
