package com.chargehub.biz.sys.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chargehub.biz.sys.domain.ChargeGunOfflineTime;
import com.chargehub.biz.sys.mapper.ChargeGunOfflineTimeMapper;
import com.chargehub.biz.sys.service.ChargeGunOfflineTimeService;
import com.chargehub.common.core.enums.ChargeGunStateEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2024/12/04 14:15
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class ChargeGunOfflineTimeServiceImpl extends ServiceImpl<ChargeGunOfflineTimeMapper, ChargeGunOfflineTime> implements ChargeGunOfflineTimeService {

    @Value("${chargehub.gun-offline:600}")
    private Long offlineThreshold;

    @Override
    public void saveOfflineTime(Integer newState, Integer oldState, Date stateTime,
                                String gunId, String regionId, String stationId) {
        if (!newState.equals(ChargeGunStateEnum.UN_KNOW.getCode()) && oldState.equals(ChargeGunStateEnum.UN_KNOW.getCode())) {
            if (stateTime == null) {
                return;
            }
            long between = DateUtil.between(stateTime, new Date(), DateUnit.SECOND);
            if (between <= offlineThreshold) {
                return;
            }
            ChargeGunOfflineTime chargeGunOfflineTime = new ChargeGunOfflineTime();
            chargeGunOfflineTime.setOfflineTime(between);
            chargeGunOfflineTime.setRegionId(regionId);
            chargeGunOfflineTime.setStationId(stationId);
            chargeGunOfflineTime.setGunId(gunId);
            this.save(chargeGunOfflineTime);
        }
    }

    @Override
    public void saveOfflineTime(Date stateTime, String gunId, String regionId, String stationId) {
        long between = 300;
        if (stateTime != null) {
            between = DateUtil.between(stateTime, new Date(), DateUnit.SECOND);
            if (between <= offlineThreshold) {
                return;
            }
            if (between > 86400) {
                between = 300;
            }
        }
        String start = LocalDate.now().atStartOfDay().format(DatePattern.NORM_DATETIME_FORMATTER);
        String end = LocalDate.now().plusDays(1).atStartOfDay().format(DatePattern.NORM_DATETIME_FORMATTER);
        ChargeGunOfflineTime one = this.lambdaQuery().eq(ChargeGunOfflineTime::getGunId, gunId)
                .ge(ChargeGunOfflineTime::getCreateTime, start)
                .lt(ChargeGunOfflineTime::getCreateTime, end).one();
        if (one != null) {
            this.lambdaUpdate()
                    .eq(ChargeGunOfflineTime::getId, one.getId())
                    .set(ChargeGunOfflineTime::getOfflineTime, one.getOfflineTime() + between)
                    .update();
        } else {
            ChargeGunOfflineTime chargeGunOfflineTime = new ChargeGunOfflineTime();
            chargeGunOfflineTime.setOfflineTime(between);
            chargeGunOfflineTime.setRegionId(regionId);
            chargeGunOfflineTime.setStationId(stationId);
            chargeGunOfflineTime.setGunId(gunId);
            this.save(chargeGunOfflineTime);
        }

    }


}
