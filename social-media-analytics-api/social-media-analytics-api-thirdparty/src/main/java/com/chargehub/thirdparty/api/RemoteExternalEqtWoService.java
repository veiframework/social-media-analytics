package com.chargehub.thirdparty.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.common.core.web.domain.AjaxResultT;
import com.chargehub.thirdparty.api.domain.dto.chwo.ChEqtWoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * RemoteEqtWoService 设备工单
 * date: 2024/12
 *
 * @author <a href="13721682347@163.com">TiAmo</a>
 */
@FeignClient(contextId = "remoteExternalEqtWoService", value = ServiceNameConstants.THIRD_PARTY_SERVICE, fallbackFactory = RemoteExternalEqtWoService.RemoteEqtWoServiceFallback.class)
public interface RemoteExternalEqtWoService {

    /**
     * 推送外部工单
     * @param chEqtWoDTO chEqtWoDTO
     * @return AjaxResultT<Object>
     */
    @PostMapping("/eqt/wo/syncChWo")
    AjaxResultT<Object> syncChWo(@RequestBody ChEqtWoDTO chEqtWoDTO);


    @Slf4j
    @Component
    class RemoteEqtWoServiceFallback implements FallbackFactory<RemoteExternalEqtWoService> {
        @Override
        public RemoteExternalEqtWoService create(Throwable cause) {
            return chEqtWoDTO -> {
                failLog(cause);
                //noinspection unchecked
                return AjaxResultT.error();
            };
        }

        public static void failLog(Throwable cause){
            String[] stackFrames = ExceptionUtils.getStackFrames(cause);
            int min = Math.min(stackFrames.length, 30);
            String caseMessage = cause.getMessage();
            String stackFramesMessage = StringUtils.join(stackFrames, ",", 0, min);
            log.error("[RemoteEqtWoService#fallback] Exception message:{} stackFrames:[{}]", caseMessage, stackFramesMessage);
        }
    }

}
