package com.chargehub.thirdparty.api.domain.dto.amap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 充电枪状态
 *
 * @author Zhanghaowei
 * @date 2024/07/30 10:18
 */
@Data
public class AliConnectorStatusInfo implements Serializable {
    private static final long serialVersionUID = -4665658210234616393L;

    @JsonProperty("ConnectorID")
    private String connectorId;

    @JsonProperty("EquipmentID")
    private String equipmentId;

    @JsonProperty("Status")
    private Integer status;

    @JsonProperty("ParkStatus")
    private Integer parkStatus;

    @JsonProperty("LockStatus")
    private Integer lockStatus;

}
