package com.chargehub.common.datascope.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 13:10
 */
@Data
@Schema
@TableName("Z9_PURVIEW")
public class Z9Purview implements Serializable {


    private static final long serialVersionUID = 4013668016984139149L;

    @TableId(value = "Z9_ID", type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "数据资源的id")
    @TableField("Z9_OWNER_ID")
    private String ownerId;

    @Schema(description = "数据权限id")
    @TableField("Z9_PURVIEW_ID")
    private String purviewId;

    @Schema(description = "层级")
    @TableField("Z9_TREE_LEVEL")
    private Integer treeLevel;

    @Schema(description = "权限类型")
    @TableField("Z9_PURVIEW_TYPE")
    private String purviewType;
}
