package com.chargehub.shop.productcategory.dto;

import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.shop.productcategory.domain.ProductCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class ProductCategoryDto implements Serializable, Z9CrudDto<ProductCategory> {
    private static final long serialVersionUID = -1892578763302801191L;

    private Integer rowNum;

    private String errorMsg;

    private String id;

    private String tenantId;

    @ApiModelProperty("顺序")
    private Integer sequence;

    @ApiModelProperty("父级id")
    private String parentId;

    @NotBlank
    @ApiModelProperty("分类名称")
    private String categoryName;

    @NotBlank
    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty(value = "id路径", hidden = true)
    private String idPath;
}
