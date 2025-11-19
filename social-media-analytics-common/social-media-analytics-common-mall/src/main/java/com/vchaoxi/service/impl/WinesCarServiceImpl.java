package com.vchaoxi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vchaoxi.entity.VcGoodsGoods;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.entity.WinesCar;
import com.vchaoxi.mapper.WinesCarMapper;
import com.vchaoxi.service.IVcGoodsGoodsService;
import com.vchaoxi.service.IWinesCarService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
@Service
public class WinesCarServiceImpl extends ServiceImpl<WinesCarMapper, WinesCar> implements IWinesCarService {

    @Autowired
    private IVcGoodsGoodsService iVcGoodsGoodsService;

    @Override
    public EditOneGoods checkCarsPrice(VcUserUser vcUserUser, String group) {
        List<WinesCar> winesCarList = this.lambdaQuery()
                .eq(WinesCar::getIsDelete, 0)
                .eq(WinesCar::getChecked, 1)
                .eq(WinesCar::getUserId, vcUserUser.getId())
                .eq(StringUtils.isNotBlank(group), WinesCar::getGoodsGroup, group)
                .list();
        EditOneGoods editOneGoods = new EditOneGoods();
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (CollectionUtils.isEmpty(winesCarList)) {
            return editOneGoods;
        }
        BigDecimal freight = BigDecimal.ZERO;
        for (WinesCar winesCar : winesCarList) {
            BigDecimal curCarGoodsPrice = winesCar.getPrice().multiply(new BigDecimal(winesCar.getNumber()));
            totalPrice = totalPrice.add(curCarGoodsPrice);
            if (vcUserUser.getMemGrade() != 4 && winesCar.getChecked().equals(1)) {
                VcGoodsGoods goods = iVcGoodsGoodsService.getById(winesCar.getGoodsId());
                BigDecimal goodsFreight = goods.getFreight().multiply(BigDecimal.valueOf(winesCar.getNumber()));
                freight = freight.add(goodsFreight);
                totalPrice = totalPrice.add(goodsFreight);
            }
        }
        editOneGoods.setTotalPrice(totalPrice);
        editOneGoods.setFreight(freight);
        return editOneGoods;
    }

    @Override
    public BigDecimal checkCarsPrice(Integer userId, String group) {
        List<WinesCar> winesCarList = this.lambdaQuery()
                .eq(WinesCar::getIsDelete, 0)
                .eq(WinesCar::getChecked, 1)
                .eq(WinesCar::getUserId, userId)
                .eq(StringUtils.isNotBlank(group), WinesCar::getGoodsGroup, group)
                .list();
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (CollectionUtils.isEmpty(winesCarList)) {
            return totalPrice;
        }
        for (WinesCar winesCar : winesCarList) {
            BigDecimal curCarGoodsPrice = winesCar.getPrice().multiply(new BigDecimal(winesCar.getNumber()));
            totalPrice = totalPrice.add(curCarGoodsPrice);
        }
        return totalPrice;
    }

    @Data
    public static class EditOneGoods {
        private BigDecimal totalPrice = BigDecimal.ZERO;

        private BigDecimal freight = BigDecimal.ZERO;
    }

}
