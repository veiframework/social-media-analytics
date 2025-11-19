package com.chargehub.biz.region.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/08/28 16:36
 */
@Data
public class RegionNode implements Serializable {
    private static final long serialVersionUID = 5587287824662220650L;

    @NotBlank
    @Schema(description = "节点id")
    private String id;

    @Schema(description = "节点父级id")
    private String parentId;


}
