package com.chargehub.admin.api.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StationDataStatisticReq {


    private String stationId;

    private String indexType;

    private String beginIndexDay;

    private String endIndexDay;
}
