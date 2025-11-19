package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.config.BaiduFeignConfig;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetAccessTokenResponse;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetSessionKeyV2Response;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetUnionIdRequest;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetUnionIdResponse;
import com.chargehub.thirdparty.api.factory.RemoteBaiduMpServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * @description: 百度API对接
 * @author: lfy
 * @create: 2024-09-09 10:02
 */
@FeignClient(contextId = "remoteBaiduMpService", url = "EMPTY", name = ServiceNameConstants.THIRD_PARTY_SERVICE, configuration = BaiduFeignConfig.class, fallbackFactory = RemoteBaiduMpServiceFallbackFactory.class)
public interface RemoteBaiduMpService {

    //@GetMapping ("/handleAccessToken")
    //@Headers("Content-Type: application/x-www-form-urlencoded")
    // GetAccessTokenResponse handleAccessToken(URI uri, @RequestHeader Map<String, String> headers, @QueryMap GetAccessTokenRequest params);

    @RequestMapping(value = "/oauth/2.0/token", method = RequestMethod.GET, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    GetAccessTokenResponse reqAccessToken(URI uri, @RequestParam("params") Map<String, Object> params);

    //@GetMapping("/handleSessionKey")
    //GetSessionKeyV2Response handleSessionKey(URI uri, @RequestHeader Map<String, String> headers, @QueryMap GetSessionKeyV2Request params);

    @RequestMapping(value = "/rest/2.0/smartapp/getsessionkey", method = RequestMethod.GET, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    GetSessionKeyV2Response reqSessionKey(URI uri, @RequestParam("params") Map<String, Object> params);

    @RequestMapping(value = "/rest/2.0/smartapp/getunionid?access_token={access_token}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    GetUnionIdResponse reqUnionId(URI uri, @PathVariable("access_token") String access_token, @RequestBody GetUnionIdRequest getUnionIdRequest);


}
