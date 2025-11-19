package com.chargehub.thirdparty.api.domain.dto.amap.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 需商家实现此查询接口，高德调用该接口获取所有全量充电站列表
 * 分页
 *
 * @author Zhanghaowei
 * @date 2024/07/30 10:50
 * @see <a href="https://y.amap.com/docs/charging/qqv7l0"/>
 */
@Data
public class AliStationInfoQueryDto implements Serializable {

    private static final long serialVersionUID = -6152459411941499914L;

    /**
     * type = page 分页查询
     */
    private String type;

    private Integer currentPage;

    private Integer pageSize;

    private List<String> idList;
}
