package com.vchaoxi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.entity.WinesCar;
import com.vchaoxi.service.impl.WinesCarServiceImpl;

import java.math.BigDecimal;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
public interface IWinesCarService extends IService<WinesCar> {


    WinesCarServiceImpl.EditOneGoods checkCarsPrice(VcUserUser vcUserUser, String group);

    public BigDecimal checkCarsPrice(Integer userId, String group);
}
