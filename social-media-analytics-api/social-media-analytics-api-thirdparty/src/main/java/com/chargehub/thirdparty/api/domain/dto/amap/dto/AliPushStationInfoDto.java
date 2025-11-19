package com.chargehub.thirdparty.api.domain.dto.amap.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 需商家实现此接口，商家按照站点id推送静态信息状态。
 * 推送场站信息
 *
 * @author Zhanghaowei
 * @date 2024/07/30 10:38
 * @see <a href="https://y.amap.com/docs/charging/cxxum6arcvt2q4fg"/>
 */
@Data
public class AliPushStationInfoDto implements Serializable {
    private static final long serialVersionUID = 4335994344722710075L;

    /**
     * 0-上线,1-下线
     * 1-时高德调用商家静态详细信息接口
     */
    private Integer status;

    private List<String> idList;

}
