package com.chargehub.thirdparty.api.domain.dto.baidu;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetAccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("error")
    private String error;

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("session_key")
    private String sessionKey;

    @JsonProperty("session_secret")
    private String sessionSecret;

}
