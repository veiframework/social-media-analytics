package com.chargehub.admin.api.model;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class StationDataStatisticRspDTO {


    private String stationId;

    private BigDecimal chargeMoney;

    private BigDecimal serviceMoney;


}
