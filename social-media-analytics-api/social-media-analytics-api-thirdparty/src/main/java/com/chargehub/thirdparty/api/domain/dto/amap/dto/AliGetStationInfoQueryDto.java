package com.chargehub.thirdparty.api.domain.dto.amap.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 需商家实现此接口，当充电站静态信息发生变化时由高德拉取商家详细信息
 *
 * @author Zhanghaowei
 * @date 2024/07/30 13:32
 * @see <a href="https://y.amap.com/docs/charging/fq7q4p6h3t7hztdv"/>
 */
@Data
public class AliGetStationInfoQueryDto implements Serializable {
    private static final long serialVersionUID = 6539846739198913868L;

    /**
     * type = page 分页查询
     */
    private String type;

    private List<String> idList;

}
