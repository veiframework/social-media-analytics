package com.chargehub.thirdparty.api.domain.dto.amap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 13:50
 */
@Data
public class AliEquipmentInfo implements Serializable {
    private static final long serialVersionUID = -620708308917812478L;

    @JsonProperty("EquipmentID")
    private String equipmentId;

    @JsonProperty("ManufacturerID")
    private String manufacturerId;

    @JsonProperty("ManufacturerName")
    private String manufacturerName;

    @JsonProperty("EquipmentModel")
    private String equipmentModel;

    @JsonProperty("ProductionDate")
    private String productionDate;

    @JsonProperty("EquipmentType")
    private Integer equipmentType;

    @JsonProperty("ConnectorInfos")
    private List<AliConnectorInfo> connectorInfos;

    @JsonProperty("EquipmentLng")
    private BigDecimal equipmentLng;

    @JsonProperty("EquipmentLat")
    private BigDecimal equipmentLat;

    @JsonProperty("Power")
    private BigDecimal power;

    @JsonProperty("EquipmentName")
    private String equipmentName;


}
