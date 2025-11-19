package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.domain.dto.amap.AliPushStationStatus;
import com.chargehub.thirdparty.api.domain.dto.amap.dto.AliMapCommonDto;
import com.chargehub.thirdparty.api.domain.dto.amap.dto.AliPushStationInfoDto;
import com.chargehub.thirdparty.api.domain.dto.amap.vo.AliMapCommonRespVo;
import com.chargehub.thirdparty.api.factory.RemoteAliMapPushServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Zhanghaowei
 * @date 2024/08/05 18:03
 */
@FeignClient(contextId = "remoteAliMapPushService", name = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteAliMapPushServiceFallbackFactory.class)
public interface RemoteAliMapPushService {

    @PostMapping("/charging/pushStationStatus")
    AliMapCommonRespVo<String> pushStationStatus(@RequestBody AliMapCommonDto<AliPushStationStatus> dto);


    @PostMapping("/charging/pushStationInfo")
    AliMapCommonRespVo<String> pushStationInfo(@RequestBody AliMapCommonDto<AliPushStationInfoDto> dto);

}
