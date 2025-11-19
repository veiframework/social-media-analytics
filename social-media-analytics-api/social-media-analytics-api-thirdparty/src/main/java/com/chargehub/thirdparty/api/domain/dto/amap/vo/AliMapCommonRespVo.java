package com.chargehub.thirdparty.api.domain.dto.amap.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 10:44
 */
@Data
public class AliMapCommonRespVo<T> implements Serializable {
    private static final long serialVersionUID = 8448373556709855772L;

    private AliMapCommonBodyVo<T> response;

    public AliMapCommonRespVo() {
    }


    public AliMapCommonRespVo(AliMapCommonBodyVo<T> response) {
        this.response = response;
    }

}
