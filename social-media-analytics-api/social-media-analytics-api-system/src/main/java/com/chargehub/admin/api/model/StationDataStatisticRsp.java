package com.chargehub.admin.api.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StationDataStatisticRsp {

    private String stationId;

    private BigDecimal chargeMoney;

    private BigDecimal serviceMoney;

    //使用月卡对应月卡的金额,考虑扣除优惠的金额
    private BigDecimal monthCardMoneyActual;

}
