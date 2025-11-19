package com.chargehub.biz.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chargehub.biz.sys.domain.ChargeGunOfflineTime;

import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2024/12/04 14:15
 */
public interface ChargeGunOfflineTimeService extends IService<ChargeGunOfflineTime> {


    /**
     * 储存离线时长
     *
     * @param stationId
     * @param gunId
     * @param regionId
     * @param oldState
     * @param newState
     * @param stateTime
     */
    void saveOfflineTime(Integer newState, Integer oldState, Date stateTime,
                         String gunId, String regionId, String stationId);

    /**
     * 保存离线数据
     *
     * @param stateTime
     * @param gunId
     * @param regionId
     * @param stationId
     */
    void saveOfflineTime(Date stateTime, String gunId, String regionId, String stationId);
}
