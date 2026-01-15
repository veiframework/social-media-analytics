package com.chargehub.shop.productcategory.vo;

import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class ProductCategoryVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = -6337998284646238530L;

    private String id;

    private String tenantId;

    @ApiModelProperty("顺序")
    private Integer sequence;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("父级id")
    private String parentId;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("路径")
    private String idPath;


}
