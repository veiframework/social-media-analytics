package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteDividePushService;
import com.chargehub.thirdparty.api.domain.dto.divide.DivideReqDTO;
import com.chargehub.thirdparty.api.domain.dto.divide.DivideRspDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.net.URI;
import java.util.List;

@Slf4j
public class RemoteDividePushServiceFallbackFactory implements FallbackFactory<RemoteDividePushService> {

    @Override
    public RemoteDividePushService create(Throwable cause) {
        log.error("清分推送:{}", cause.getMessage(), cause);
        return new RemoteDividePushService() {

            @Override
            public DivideRspDTO pushDivideBatch(URI uri, List<DivideReqDTO> reqDTO) {
                return null;
            }
        };
    }
}
