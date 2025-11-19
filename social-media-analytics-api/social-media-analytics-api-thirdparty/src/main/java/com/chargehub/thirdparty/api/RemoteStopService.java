package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.dto.stop.StopFeeDto;
import com.chargehub.thirdparty.api.domain.vo.stop.ReduceStopFeeVo;
import com.chargehub.thirdparty.api.factory.RemoteWxOpenFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/12 10:09
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api
 * @Filename：RemoteStopService
 */
@FeignClient(contextId = "remoteStopService", value = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteWxOpenFallbackFactory.class)
public interface RemoteStopService {


    /**
     * 充电抵扣停车费
     * @param stopFeeDto
     * @return
     */
    @PostMapping("/stop/reduce")
    public ReduceStopFeeVo reduceStopFee(@RequestBody @Validated StopFeeDto stopFeeDto);
}
