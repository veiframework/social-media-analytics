package com.chargehub.thirdparty.api.domain.dto.amap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 13:48
 */
@Data
public class AliDiscountPriceChargingInfo implements Serializable {
    private static final long serialVersionUID = -8686756323141027263L;

    @JsonProperty("DiscountTime")
    private String discountTime;

    @JsonProperty("DiscountElectricityFee")
    private BigDecimal discountElectricityFee;

    @JsonProperty("DiscountServiceFee")
    private BigDecimal discountServiceFee;


}
