package com.chargehub.thirdparty.api.factory;

import com.chargehub.thirdparty.api.RemoteWxLogisticsService;
import com.chargehub.thirdparty.api.domain.dto.logistics.*;
import com.chargehub.thirdparty.api.domain.vo.logistics.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author Zhanghaowei
 * @date 2025/08/25 16:35
 */
@Slf4j
public class RemoteWxLogisticsServiceFallbackFactory implements FallbackFactory<RemoteWxLogisticsService> {
    @Override
    public RemoteWxLogisticsService create(Throwable cause) {
        log.error("微信物流助手请求失败:{}", cause.getMessage());
        return new RemoteWxLogisticsService() {

            @Override
            public LoAccountBindVo accountBind(String maAppId, LoAccountBindParam loAccountBindParam) {
                return null;
            }

            @Override
            public LoAccountGetallVo accountGetall(String maAppId) {
                return null;
            }

            @Override
            public LoDeliveryGetallVo deliveryGetall(String maAppId) {
                return null;
            }

            @Override
            public LoOrderCancelVo orderCancel(String maAppId, LoOrderCancelParam loOrderCancelParam) {
                return null;
            }

            @Override
            public LoQuotaGetVo quotaGet(String maAppId, LoQuotaGetParam loQuotaGetParam) {
                return null;
            }

            @Override
            public LoOrderGetVo orderGet(String maAppId, LoOrderGetParam loOrderGetParam) {
                return null;
            }

            @Override
            public LoPathGetVo pathGet(String maAppId, LoPathGetParam loPathGetParam) {
                return null;
            }

            @Override
            public LoOrderAddVo orderAdd(String maAppId, LoOrderAddParam loOrderAddParam) {
                return null;
            }
        };
    }
}
