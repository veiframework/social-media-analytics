package com.chargehub.thirdparty.api.domain.dto.amap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 14:04
 */
@Data
public class AliConnectorInfo implements Serializable {
    private static final long serialVersionUID = -829469259116735585L;

    @JsonProperty("ConnectorID")
    private String connectorId;

    @JsonProperty("ConnectorName")
    private String connectorName;

    @JsonProperty("ConnectorType")
    private Integer connectorType;

    @JsonProperty("VoltageUpperLimits")
    private Integer voltageUpperLimits;

    @JsonProperty("VoltageLowerLimits")
    private Integer voltageLowerLimits;

    @JsonProperty("Current")
    private Integer current;

    @JsonProperty("Power")
    private BigDecimal power;

    @JsonProperty("ParkNo")
    private String parkNo;

    @JsonProperty("NationalStandard")
    private Integer nationalStandard;

    @JsonProperty("BrandDesc")
    private String brandDesc;



}
