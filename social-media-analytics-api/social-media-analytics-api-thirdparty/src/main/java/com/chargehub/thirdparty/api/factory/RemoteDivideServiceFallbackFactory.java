package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteDivideService;
import com.chargehub.thirdparty.api.domain.dto.divide.DivideReqDTO;
import com.chargehub.thirdparty.api.domain.dto.divide.DivideRspDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

@Slf4j
public class RemoteDivideServiceFallbackFactory implements FallbackFactory<RemoteDivideService> {

    @Override
    public RemoteDivideService create(Throwable cause) {
        log.error(cause.getMessage(), cause);
        return new RemoteDivideService() {

            @Override
            public DivideRspDTO reqDivideBatch(List<DivideReqDTO> reqDTOList) {
                return null;
            }
        };
    }
}
