package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteAliMapChargeService;
import com.chargehub.thirdparty.api.domain.dto.amap.dto.AliMapCommonDto;
import com.chargehub.thirdparty.api.domain.dto.amap.vo.AliMapCommonBodyVo;
import com.chargehub.thirdparty.api.domain.dto.amap.vo.AliMapCommonRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.net.URI;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/07/29 15:44
 */
@Slf4j
public class RemoteAliMapChargeServiceFallbackFactory implements FallbackFactory<RemoteAliMapChargeService> {

    @Override
    public RemoteAliMapChargeService create(Throwable cause) {
        log.error("高德推送失败:{}", cause.getMessage());
        return new RemoteAliMapChargeService() {


            @Override
            public <T> AliMapCommonRespVo<T> pushMessage(URI uri, Map<String, String> headers, AliMapCommonDto<?> dto) {
                AliMapCommonBodyVo<T> bodyVo = new AliMapCommonBodyVo<>();
                bodyVo.setCode("40004");
                bodyVo.setMsg(cause.getMessage());
                return new AliMapCommonRespVo<>(bodyVo);
            }


        };
    }

}
