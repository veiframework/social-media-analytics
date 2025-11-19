package com.vchaoxi.service.impl;

import cn.hutool.core.lang.Assert;
import com.vchaoxi.entity.VcShopShop;
import com.vchaoxi.entity.VcShopWx;
import com.vchaoxi.logistic.domain.ShopRelativeId;
import com.vchaoxi.logistic.service.ShopExtendService;
import com.vchaoxi.service.IVcShopShopService;
import com.vchaoxi.service.IVcShopWxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zhanghaowei
 * @date 2025/08/25 17:29
 */
@Service
public class MallShopExtendServiceImpl implements ShopExtendService {

    @Autowired
    private IVcShopWxService shopWxService;

    @Autowired
    private IVcShopShopService shopShopService;

    @Override
    public ShopRelativeId getMaAppId(Integer shopId) {
        VcShopWx shopWx = shopWxService.lambdaQuery().eq(VcShopWx::getShopId, shopId).one();
        Assert.notNull(shopWx, "商铺微信配置不存在!");
        VcShopShop shop = shopShopService.lambdaQuery().eq(VcShopShop::getId, shopId).one();
        Assert.notNull(shop, "商铺信息不存在!");
        return new ShopRelativeId(shopWx.getMpAppid(), shop.getAgentId());
    }

    @Override
    public void updateOpenLogistics(Integer shopId, Integer status) {
        shopShopService.lambdaUpdate()
                .eq(VcShopShop::getId, shopId)
                .set(VcShopShop::getOpenLogistics, status)
                .update();
    }

    @Override
    public Integer getOpenLogisticsStatus(Integer shopId) {
        VcShopShop shop = shopShopService.lambdaQuery().eq(VcShopShop::getId, shopId).one();
        Assert.notNull(shop, "商铺信息不存在!");
        return shop.getOpenLogistics();
    }

}
