package com.chargehub.thirdparty.domain.chwo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;


/**
 * ch 工单系统请求
 * date: 2024/12
 *
 * @author <a href="13721682347@163.com">TiAmo</a>
 */
@Data
public class ChWoRequestDTO {

    @JsonProperty(value = "request_parameters", index = 1)
    private Map<String, Object> requestParameters;

    @JsonProperty(index = 2)
    private String timestamp;

    @JsonProperty(index = 3)
    private String random;

    @JsonProperty(index = 4)
    private String sign;
}
