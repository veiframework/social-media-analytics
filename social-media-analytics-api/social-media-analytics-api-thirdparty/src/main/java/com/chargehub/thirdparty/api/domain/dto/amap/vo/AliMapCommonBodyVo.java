package com.chargehub.thirdparty.api.domain.dto.amap.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 14:48
 */
@Data
public class AliMapCommonBodyVo<T> implements Serializable {

    private static final long serialVersionUID = 286276715813019796L;

    private String code;

    private String msg;

    private T data;

    private String subCode;

    private String subMsg;

}
