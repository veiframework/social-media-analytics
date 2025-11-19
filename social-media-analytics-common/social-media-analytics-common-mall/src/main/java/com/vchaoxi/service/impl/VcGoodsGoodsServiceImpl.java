package com.vchaoxi.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vchaoxi.entity.VcGoodsGoods;
import com.vchaoxi.entity.WinesGoodsSpecification;
import com.vchaoxi.mapper.VcGoodsGoodsMapper;
import com.vchaoxi.param.GoodsParam;
import com.vchaoxi.param.SpecificationParam;
import com.vchaoxi.service.IVcGoodsGoodsService;
import com.vchaoxi.service.IWinesGoodsSpecificationService;
import com.vchaoxi.vo.CommonResult;
import com.vchaoxi.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品相关 - 商品表 服务实现类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Service
public class VcGoodsGoodsServiceImpl extends ServiceImpl<VcGoodsGoodsMapper, VcGoodsGoods> implements IVcGoodsGoodsService {


    @Autowired
    private IWinesGoodsSpecificationService iWinesGoodsSpecificationService;

    /**
     * 添加商品信息
     * @param goodsParam
     * @return
     */
    @Override
    @Transactional
    public CommonResult add(GoodsParam goodsParam) {
        //查询商家商品数量
        Long count = lambdaQuery().eq(VcGoodsGoods::getIsDelete,0).eq(VcGoodsGoods::getShopId,goodsParam.getShopId()).count();
        VcGoodsGoods vcGoodsGoods = new VcGoodsGoods();
        vcGoodsGoods.setAgentId(goodsParam.getAgentId());
        vcGoodsGoods.setShopId(goodsParam.getShopId());
        vcGoodsGoods.setType(goodsParam.getType());
        vcGoodsGoods.setName(goodsParam.getName());
        vcGoodsGoods.setFreight(goodsParam.getFreight());
        vcGoodsGoods.setGoodsNo(goodsParam.getGoodsNo());
        vcGoodsGoods.setStatus(1);
        vcGoodsGoods.setImg(goodsParam.getImg());
        //设置宣传图片
        if (!CollectionUtils.isEmpty(goodsParam.getGalleryList())) {
            String gallerysStr  = String.join(",", goodsParam.getGalleryList());
            vcGoodsGoods.setGallery(gallerysStr);
        }
        vcGoodsGoods.setAmount(goodsParam.getAmount());
        vcGoodsGoods.setMemAmount(goodsParam.getMemAmount());
        vcGoodsGoods.setVipAmount(goodsParam.getVipAmount());
        vcGoodsGoods.setSerial(count.intValue() + 1);
        vcGoodsGoods.setUnit(goodsParam.getUnit());
        vcGoodsGoods.setDetails(goodsParam.getDetails());
        vcGoodsGoods.setBrief(goodsParam.getBrief());
        save(vcGoodsGoods);

        List<WinesGoodsSpecification> specificationList = new ArrayList<>();
        for (SpecificationParam specificationParam: goodsParam.getGoodsSpecificationVoList()) {
            WinesGoodsSpecification goodsSpecification = new WinesGoodsSpecification();
            goodsSpecification.setGoodsId(vcGoodsGoods.getId());
            goodsSpecification.setSpecification(specificationParam.getSpecification());
            goodsSpecification.setValue(specificationParam.getValue());
            goodsSpecification.setPicUrl(specificationParam.getPicUrl());
            goodsSpecification.setAmount(specificationParam.getAmount());
            goodsSpecification.setMemAmount(specificationParam.getMemAmount());
            goodsSpecification.setVipAmount(specificationParam.getVipAmount());
            specificationList.add(goodsSpecification);
        }
        iWinesGoodsSpecificationService.saveBatch(specificationList);
        return CommonResult.success();
    }




    /**
     * 编辑商品信息
     * @param goodsParam
     * @param goodsVo
     * @return
     */
    @Override
    @Transactional
    public CommonResult edit(GoodsParam goodsParam, GoodsVo goodsVo) {
        String gallerysStr = "";
        if (!CollectionUtils.isEmpty(goodsParam.getGalleryList())) {
            gallerysStr  = String.join(",", goodsParam.getGalleryList());
        }

        lambdaUpdate().eq(VcGoodsGoods::getId,goodsParam.getId())
                .set(VcGoodsGoods::getType,goodsParam.getType())
                .set(VcGoodsGoods::getName,goodsParam.getName())
                .set(VcGoodsGoods::getGoodsNo,goodsParam.getGoodsNo())
                .set(VcGoodsGoods::getImg,goodsParam.getImg())
                .set(VcGoodsGoods::getGallery,gallerysStr)
                .set(VcGoodsGoods::getAmount,goodsParam.getAmount())
                .set(VcGoodsGoods::getMemAmount,goodsParam.getMemAmount())
                .set(VcGoodsGoods::getVipAmount,goodsParam.getVipAmount())
                .set(VcGoodsGoods::getSerial,goodsParam.getSerial())
                .set(VcGoodsGoods::getUnit,goodsParam.getUnit())
                .set(VcGoodsGoods::getBrief,goodsParam.getBrief())
                .set(VcGoodsGoods::getDetails,goodsParam.getDetails())
                .set(VcGoodsGoods::getFreight,goodsParam.getFreight())
                .update();

        //商品重新排序
        goodsSorting(goodsVo.getShopId());
        List<WinesGoodsSpecification> oldGoodsSpce = iWinesGoodsSpecificationService.lambdaQuery()
                .eq(WinesGoodsSpecification::getIsDelete,0)
                .eq(WinesGoodsSpecification::getGoodsId,goodsParam.getId()).list();
        for (WinesGoodsSpecification oldGoodsSpecification:oldGoodsSpce){
            oldGoodsSpecification.setIsDelete(1);
        }
        iWinesGoodsSpecificationService.updateBatchById(oldGoodsSpce);

        List<WinesGoodsSpecification> specificationList = new ArrayList<>();
        for (SpecificationParam specificationParam: goodsParam.getGoodsSpecificationVoList()) {
            WinesGoodsSpecification goodsSpecification = new WinesGoodsSpecification();
            goodsSpecification.setGoodsId(goodsParam.getId());
            goodsSpecification.setSpecification(specificationParam.getSpecification());
            goodsSpecification.setValue(specificationParam.getValue());
            goodsSpecification.setPicUrl(specificationParam.getPicUrl());
            goodsSpecification.setAmount(specificationParam.getAmount());
            goodsSpecification.setMemAmount(specificationParam.getMemAmount());
            goodsSpecification.setVipAmount(specificationParam.getVipAmount());
            specificationList.add(goodsSpecification);
        }
        iWinesGoodsSpecificationService.saveBatch(specificationList);
        return CommonResult.success();
    }



    /**
     * 删除商品信息
     * @param id
     * @param goodsVo
     * @return
     */
    @Override
    @Transactional
    public CommonResult del(Integer id,GoodsVo goodsVo) {
        lambdaUpdate().eq(VcGoodsGoods::getId,id).set(VcGoodsGoods::getIsDelete,1).set(VcGoodsGoods::getDeleteTime, LocalDateTime.now()).update();
        List<WinesGoodsSpecification> oldGoodsSpce = iWinesGoodsSpecificationService.lambdaQuery()
                .eq(WinesGoodsSpecification::getIsDelete,0).eq(WinesGoodsSpecification::getGoodsId,id).list();
        for (WinesGoodsSpecification goodsSpecification :oldGoodsSpce){
            goodsSpecification.setIsDelete(1);
        }
        iWinesGoodsSpecificationService.updateBatchById(oldGoodsSpce);
        //商品重新排序
        goodsSorting(goodsVo.getShopId());
        return CommonResult.success();
    }





    /**
     * 商品排序
     * @param shopId
     */
    private void goodsSorting(Integer shopId){
        List<VcGoodsGoods> list = lambdaQuery().eq(VcGoodsGoods::getIsDelete,0).eq(VcGoodsGoods::getShopId,shopId)
                .orderByAsc(VcGoodsGoods::getSerial).list();
        Integer serial = 1;
        for(VcGoodsGoods vcGoodsGoods : list){
            lambdaUpdate().eq(VcGoodsGoods::getId,vcGoodsGoods.getId())
                    .set(VcGoodsGoods::getSerial,serial).update();

            serial ++;
        }
    }
}
