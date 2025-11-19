package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteStopService;
import com.chargehub.thirdparty.api.domain.dto.stop.StopFeeDto;
import com.chargehub.thirdparty.api.domain.vo.stop.ReduceStopFeeVo;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/12 10:10
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.factory
 * @Filename：RemoteStopFallbackFactory
 */
@Component
public class RemoteStopFallbackFactory implements FallbackFactory<RemoteStopService> {
    @Override
    public RemoteStopService create(Throwable cause) {
        return new RemoteStopService() {
            @Override
            public ReduceStopFeeVo reduceStopFee(StopFeeDto stopFeeDto) {
                return null;
            }
        };
    }
}
