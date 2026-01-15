package com.chargehub.shop.productcategory.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.common.security.template.event.Z9BeforeDeleteEvent;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.chargehub.shop.productcategory.domain.ProductCategory;
import com.chargehub.shop.productcategory.dto.ProductCategoryDto;
import com.chargehub.shop.productcategory.mapper.ProductCategoryMapper;
import com.chargehub.shop.productcategory.vo.ProductCategoryVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProductCategoryService extends AbstractZ9CrudServiceImpl<ProductCategoryMapper, ProductCategory> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    protected ProductCategoryService(ProductCategoryMapper baseMapper) {
        super(baseMapper);
    }

    @Override
    public void create(Z9CrudDto<ProductCategory> dto) {
        ProductCategoryDto productCategoryDto = (ProductCategoryDto) dto;
        String parentId = productCategoryDto.getParentId();
        ProductCategory parentCategory = this.baseMapper.getParentIdPath(parentId);
        String id = IdWorker.getIdStr();
        productCategoryDto.setId(id);
        if (parentCategory == null) {
            productCategoryDto.setIdPath(id);
        } else {
            String idPath = parentCategory.getIdPath();
            productCategoryDto.setIdPath(idPath + "," + id);
        }
        super.create(dto);
    }

    @Override
    public void edit(Z9CrudDto<ProductCategory> dto, String id) {
        ProductCategoryDto productCategoryDto = (ProductCategoryDto) dto;
        String parentId = productCategoryDto.getParentId();
        ProductCategory parentCategory = this.baseMapper.getParentIdPath(parentId);
        if (parentCategory == null) {
            productCategoryDto.setIdPath(id);
        } else {
            String idPath = parentCategory.getIdPath();
            productCategoryDto.setIdPath(idPath + "," + id);
        }
        super.edit(dto, id);
        this.baseMapper.updateState(id, productCategoryDto.getState());
    }


    public void deleteByIdPath(String id) {
        List<ProductCategory> children = this.baseMapper.getChildren(id);
        if (CollectionUtils.isNotEmpty(children)) {
            List<String> collect = children.stream().map(ProductCategory::getId).collect(Collectors.toList());
            applicationContext.publishEvent(new Z9BeforeDeleteEvent<>(collect, ProductCategory.class));
        }
        this.baseMapper.deleteByIdPath(id);
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return ProductCategoryDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return ProductCategoryVo.class;
    }

    @Override
    public String excelName() {
        return "商品分类";
    }
}
