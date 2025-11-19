package com.chargehub.thirdparty.api.domain.dto.amap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 需商家实现此查询接口，有动态数据变更时实时推送高德获取全量充电设备状态信息
 *
 * @author Zhanghaowei
 * @date 2024/07/29 16:14
 * @see <a href="https://y.amap.com/docs/charging/qftk7o"/>
 */
@Data
public class AliPushStationStatus implements Serializable {

    private static final long serialVersionUID = 5495367263727705733L;

    @JsonProperty("stationID")
    private String stationId;

    @JsonProperty("super_free")
    private Integer superFree;

    @JsonProperty("super_total")
    private Integer superTotal;

    @JsonProperty("fast_free")
    private Integer fastFree;

    @JsonProperty("fast_total")
    private Integer fastTotal;

    @JsonProperty("slow_free")
    private Integer slowFree;

    @JsonProperty("slow_total")
    private Integer slowTotal;

    private List<AliConnectorStatusInfo> connectorStatusInfo;

}
