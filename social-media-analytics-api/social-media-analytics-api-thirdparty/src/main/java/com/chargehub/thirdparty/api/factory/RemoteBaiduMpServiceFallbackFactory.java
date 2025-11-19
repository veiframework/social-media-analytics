package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteBaiduMpService;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetAccessTokenResponse;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetSessionKeyV2Response;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetUnionIdRequest;
import com.chargehub.thirdparty.api.domain.dto.baidu.GetUnionIdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.net.URI;
import java.util.Map;

/**
 * @description: 百度
 * @author: lfy
 * @create: 2024-09-09 10:07
 */
@Slf4j
public class RemoteBaiduMpServiceFallbackFactory implements FallbackFactory<RemoteBaiduMpService> {

    @Override
    public RemoteBaiduMpService create(Throwable cause) {
        log.error("百度API:{}", cause.getMessage(), cause);
        return new RemoteBaiduMpService() {

            @Override
            public GetAccessTokenResponse reqAccessToken(URI uri, Map request) {
                log.error("handleAccessToken调用失败");
                return null;
            }

            @Override
            public GetSessionKeyV2Response reqSessionKey(URI uri, Map<String, Object> params) {
                log.error("handleSessionKey调用失败");
                return new GetSessionKeyV2Response();
            }

            @Override
            public GetUnionIdResponse reqUnionId(URI uri, String access_token, GetUnionIdRequest getUnionIdRequest) {
                log.error("handleUnionId调用失败");
                return null;
            }

        };
    }
}
