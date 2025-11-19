package com.vchaoxi.param;

import lombok.Data;

import java.util.List;

@Data
public class ZjsLogisticsOrderParam {
    private String clientFlag;
    private List<ZjsLogisticsParam> orders;
}
