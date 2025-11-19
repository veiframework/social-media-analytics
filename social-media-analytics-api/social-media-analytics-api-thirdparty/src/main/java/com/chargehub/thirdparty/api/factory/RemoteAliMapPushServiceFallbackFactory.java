package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteAliMapPushService;
import com.chargehub.thirdparty.api.domain.dto.amap.AliPushStationStatus;
import com.chargehub.thirdparty.api.domain.dto.amap.dto.AliMapCommonDto;
import com.chargehub.thirdparty.api.domain.dto.amap.dto.AliPushStationInfoDto;
import com.chargehub.thirdparty.api.domain.dto.amap.vo.AliMapCommonBodyVo;
import com.chargehub.thirdparty.api.domain.dto.amap.vo.AliMapCommonRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author Zhanghaowei
 * @date 2024/08/05 18:04
 */
@Slf4j
public class RemoteAliMapPushServiceFallbackFactory implements FallbackFactory<RemoteAliMapPushService> {
    @Override
    public RemoteAliMapPushService create(Throwable cause) {
        log.error("商家推送高德失败:{}", cause.getMessage());
        return new RemoteAliMapPushService() {

            @Override
            public AliMapCommonRespVo<String> pushStationStatus(AliMapCommonDto<AliPushStationStatus> dto) {
                AliMapCommonBodyVo<String> bodyVo = new AliMapCommonBodyVo<>();
                bodyVo.setCode("40004");
                bodyVo.setMsg(cause.getMessage());
                return new AliMapCommonRespVo<>(bodyVo);
            }

            @Override
            public AliMapCommonRespVo<String> pushStationInfo(AliMapCommonDto<AliPushStationInfoDto> dto) {
                AliMapCommonBodyVo<String> bodyVo = new AliMapCommonBodyVo<>();
                bodyVo.setCode("40004");
                bodyVo.setMsg(cause.getMessage());
                return new AliMapCommonRespVo<>(bodyVo);
            }

        };
    }
}
