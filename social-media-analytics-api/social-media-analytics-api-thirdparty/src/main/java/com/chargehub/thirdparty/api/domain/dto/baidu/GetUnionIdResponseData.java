package com.chargehub.thirdparty.api.domain.dto.baidu;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetUnionIdResponseData {

    @JsonProperty("unionid")
    private String unionid;
}
