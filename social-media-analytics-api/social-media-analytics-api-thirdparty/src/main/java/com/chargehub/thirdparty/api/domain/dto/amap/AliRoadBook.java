package com.chargehub.thirdparty.api.domain.dto.amap;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 13:52
 */
@Data
public class AliRoadBook implements Serializable {
    private static final long serialVersionUID = -8505331402512993017L;

    private String number;

    private String pictures;

    private String picDescription;

}
