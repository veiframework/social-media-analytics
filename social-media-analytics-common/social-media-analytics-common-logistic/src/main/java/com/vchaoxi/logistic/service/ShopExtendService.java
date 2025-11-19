package com.vchaoxi.logistic.service;

import com.vchaoxi.logistic.domain.ShopRelativeId;

/**
 * @author Zhanghaowei
 * @date 2025/08/25 17:10
 */
public interface ShopExtendService {


    /**
     * 获取商户的appId
     *
     * @param shopId
     * @return
     */
    ShopRelativeId getMaAppId(Integer shopId);

    /**
     * 更新物流开启状态
     *
     * @param status
     * @param shopId
     */
    void updateOpenLogistics(Integer shopId, Integer status);

    /**
     * 获取商户物流开启状态
     *
     * @param shopId
     * @return
     */
    Integer getOpenLogisticsStatus(Integer shopId);

}
