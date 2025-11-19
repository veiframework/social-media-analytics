package com.chargehub.thirdparty.api.domain.dto.baidu;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetSessionKeyV2ResponseData {

    @JsonProperty("open_id")
    private String openId;

    @JsonProperty("session_key")
    private String sessionKey;
}
