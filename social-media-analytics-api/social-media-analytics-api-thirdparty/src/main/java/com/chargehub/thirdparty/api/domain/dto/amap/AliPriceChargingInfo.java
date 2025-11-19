package com.chargehub.thirdparty.api.domain.dto.amap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 13:47
 */
@Data
public class AliPriceChargingInfo implements Serializable {
    private static final long serialVersionUID = 5267339389474945408L;

    @JsonProperty("FeeTime")
    private String feeTime;

    @JsonProperty("ElectricityFee")
    private BigDecimal electricityFee;

    @JsonProperty("ServiceFee")
    private BigDecimal serviceFee;


}
