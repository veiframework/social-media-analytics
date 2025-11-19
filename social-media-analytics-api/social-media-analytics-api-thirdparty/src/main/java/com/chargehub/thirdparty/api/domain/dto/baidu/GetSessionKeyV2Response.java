package com.chargehub.thirdparty.api.domain.dto.baidu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetSessionKeyV2Response {

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("error_msg")
    private String errorMsg;

    @JsonProperty("errno")
    private Integer errno;

    @JsonProperty("errmsg")
    private String errmsg;

    @JsonProperty("timestamp")
    private Integer timestamp;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("data")
    private GetSessionKeyV2ResponseData data;

}
