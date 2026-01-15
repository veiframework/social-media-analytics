package com.chargehub.shop.productcategory.mapper;

import com.chargehub.common.security.template.mybatis.Z9MpCrudMapper;
import com.chargehub.shop.productcategory.domain.ProductCategory;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Mapper
public interface ProductCategoryMapper extends Z9MpCrudMapper<ProductCategory> {


    @Update("update product_category set state = #{state} where FIND_IN_SET(#{id}, id_path)")
    void updateState(@Param("id") String id, @Param("state") String state);

    @Delete("delete from product_category where FIND_IN_SET(#{id}, id_path)")
    void deleteByIdPath(String id);

    default ProductCategory getParentIdPath(String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return null;
        }
        return this.lambdaQuery().eq(ProductCategory::getId, parentId).one();
    }

    @Select("select * from product_category where FIND_IN_SET(#{id}, id_path)")
    List<ProductCategory> getChildren(String id);

}
