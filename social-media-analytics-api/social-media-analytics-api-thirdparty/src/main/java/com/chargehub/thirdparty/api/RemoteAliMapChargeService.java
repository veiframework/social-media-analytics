package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.thirdparty.api.config.AliMapFeignConfig;
import com.chargehub.thirdparty.api.domain.dto.amap.dto.AliMapCommonDto;
import com.chargehub.thirdparty.api.domain.dto.amap.vo.AliMapCommonRespVo;
import com.chargehub.thirdparty.api.factory.RemoteAliMapChargeServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/07/29 15:41
 */
@FeignClient(contextId = "remoteAlipayMapChargeService", configuration = AliMapFeignConfig.class, url = "EMPTY", name = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteAliMapChargeServiceFallbackFactory.class)
public interface RemoteAliMapChargeService {

    /**
     * 推送所有或单个设备状态信息
     *
     * @param uri
     * @param headers
     * @param dto
     * @return
     */
    @PostMapping
   <T> AliMapCommonRespVo<T> pushMessage(URI uri, @RequestHeader Map<String, String> headers, @RequestBody AliMapCommonDto<?> dto);


}
