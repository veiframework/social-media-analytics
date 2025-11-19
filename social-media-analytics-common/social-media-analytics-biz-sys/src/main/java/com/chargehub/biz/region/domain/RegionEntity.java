package com.chargehub.biz.region.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.chargehub.common.core.constant.PurviewConstants;
import com.chargehub.common.security.template.annotation.CrudPurviewField;
import com.chargehub.common.security.template.annotation.CrudSubUniqueId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2021-11-05 14:42:56
 */
@Data
@TableName(value = "charge_region")
@Schema
public class RegionEntity implements Serializable, Z9CrudEntity {


    private static final long serialVersionUID = -7748711768096828399L;

    @CrudPurviewField(purviewTypes = {PurviewConstants.USER_REGION_PURVIEW})
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;

    @Schema(description = "租户id")
    @TableField("TENANT_ID")
    private String tenantId;

    @Schema(description = "父级主键")
    @TableField("PARENT_ID")
    private String parentId;

    @CrudSubUniqueId
    @Schema(description = "标签名")
    @TableField("LABEL")
    private String label;

    @Schema(description = "备注")
    @TableField("REMARK")
    private String remark;

    @CrudSubUniqueId
    @Schema(description = "层级")
    @TableField("LEVEL")
    private Integer level;

    @Schema(description = "是否为最后一级")
    @TableField("LAST_STAGE")
    private Boolean lastStage;

    @CrudSubUniqueId
    @Schema(description = "区域编码")
    private String areaCode;

    @Schema(description = "是否可见0-不可见,1-可见")
    private String visible;


    @TableLogic
    private Boolean deleted;

    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}
