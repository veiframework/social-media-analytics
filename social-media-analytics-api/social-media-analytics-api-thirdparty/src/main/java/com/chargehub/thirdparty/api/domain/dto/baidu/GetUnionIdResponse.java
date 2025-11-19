package com.chargehub.thirdparty.api.domain.dto.baidu;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetUnionIdResponse {

    @JsonProperty("data")
    private GetUnionIdResponseData data;

    @JsonProperty("errmsg")
    private String errmsg;

    @JsonProperty("errno")
    private long errno;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("timestamp")
    private long timestamp;

}
