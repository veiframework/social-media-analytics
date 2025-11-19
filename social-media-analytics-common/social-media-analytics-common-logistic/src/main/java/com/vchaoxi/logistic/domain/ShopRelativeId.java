package com.vchaoxi.logistic.domain;

import lombok.Data;

/**
 * @author Zhanghaowei
 * @date 2025/08/25 17:25
 */
@Data
public class ShopRelativeId {


    private String maAppId;

    private Integer agentId;

    public ShopRelativeId() {
    }


    public ShopRelativeId(String maAppId, Integer agentId) {
        this.maAppId = maAppId;
        this.agentId = agentId;
    }
}
