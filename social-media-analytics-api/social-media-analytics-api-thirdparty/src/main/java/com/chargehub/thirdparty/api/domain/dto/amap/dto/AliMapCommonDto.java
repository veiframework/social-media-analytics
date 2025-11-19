package com.chargehub.thirdparty.api.domain.dto.amap.dto;

import com.alibaba.nacos.common.utils.JacksonUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/07/29 16:15
 */
@Data
public class AliMapCommonDto<T> implements Serializable {
    private static final long serialVersionUID = -3985265466054304047L;

    private String utc_timestamp;

    private String version;

    private String charset;

    private String method;

    private String sign;

    private String sign_type;

    private String app_id;

    private String biz_content;

    public void bizContent(T bizContent) {
        this.biz_content = JacksonUtils.toJson(bizContent);
    }

    public T bizContent(Class<T> tClass) {
        return JacksonUtils.toObj(this.biz_content, tClass);
    }


}
