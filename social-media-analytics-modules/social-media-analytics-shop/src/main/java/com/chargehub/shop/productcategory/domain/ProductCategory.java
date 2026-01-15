package com.chargehub.shop.productcategory.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.annotation.CrudSubUniqueId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class ProductCategory implements Serializable, Z9CrudEntity {
    private static final long serialVersionUID = 7660867456974231751L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String tenantId;

    @ApiModelProperty("顺序")
    private Integer sequence;

    @CrudSubUniqueId
    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("父级id")
    private String parentId;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("id路径")
    private String idPath;


    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}
