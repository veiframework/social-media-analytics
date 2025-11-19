package com.chargehub.thirdparty.api.domain.dto.baidu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetSessionKeyV2Request {

    @JsonProperty("code")
    private String code;

    @JsonProperty("access_token")
    private String accessToken;

    //@SerializedName("client_id")
    //private String clientId = "";

    //@SerializedName("sk")
    //private String sk = "";
}
