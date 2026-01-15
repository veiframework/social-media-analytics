package com.chargehub.shop.productcategory.controller;

import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.utils.SecurityUtils;
import com.chargehub.shop.productcategory.dto.ProductCategoryDto;
import com.chargehub.shop.productcategory.dto.ProductCategoryQueryDto;
import com.chargehub.shop.productcategory.service.ProductCategoryService;
import com.chargehub.shop.productcategory.vo.ProductCategoryVo;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.xml.core.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@RestController
@UnifyResult
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Debounce
    @RequiresPermissions("product:category:create")
    @PostMapping("/shop/product-category")
    public void create(@RequestBody @Validate ProductCategoryDto dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Integer shopId = loginUser.getShopId();
        dto.setTenantId(shopId + "");
        productCategoryService.create(dto);
    }

    @Debounce
    @RequiresPermissions("product:category:edit")
    @PutMapping("/shop/product-category")
    public void edit(@RequestBody @Validate ProductCategoryDto dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Integer shopId = loginUser.getShopId();
        dto.setTenantId(shopId + "");
        productCategoryService.edit(dto, dto.getId());
    }


    @RequiresPermissions("product:category:all")
    @SuppressWarnings("unchecked")
    @GetMapping("/shop/product-category/all")
    public List<ProductCategoryVo> getAll(ProductCategoryQueryDto queryDto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Integer shopId = loginUser.getShopId();
        queryDto.setTenantId(shopId + "");
        return (List<ProductCategoryVo>) productCategoryService.getAll(queryDto);
    }


    @Debounce
    @RequiresPermissions("product:category:delete")
    @DeleteMapping("/shop/product-category/{id}")
    public void deleteByIdPath(@PathVariable String id) {
        productCategoryService.deleteByIdPath(id);
    }

}
