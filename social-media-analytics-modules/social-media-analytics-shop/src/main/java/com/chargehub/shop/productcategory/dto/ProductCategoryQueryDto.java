package com.chargehub.shop.productcategory.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chargehub.common.datasource.mybatis.Pagination;
import com.chargehub.common.security.template.annotation.CrudQueryField;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.shop.productcategory.domain.ProductCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductCategoryQueryDto extends Pagination implements Serializable, Z9CrudQueryDto<ProductCategory> {

    private static final long serialVersionUID = 3851812367659563050L;

    @CrudQueryField
    private String tenantId;

    @Override
    public Page<ProductCategory> buildPageObj() {
        return page();
    }
}
