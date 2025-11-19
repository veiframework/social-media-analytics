package com.chargehub.admin.api.factory;

import com.chargehub.admin.api.RemoteAppUserService;

import com.chargehub.admin.api.domain.dto.WxLoginDto;
import com.chargehub.common.core.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author Zhanghaowei
 * @date 2025/08/02 17:50
 */
@Slf4j
public class RemoteAppUserServiceFallbackFactory implements FallbackFactory<RemoteAppUserService> {


    @Override
    public RemoteAppUserService create(Throwable cause) {
        log.error("competition-user服务调用失败:{}", cause.getMessage());
        return new RemoteAppUserService() {


            @Override
            public AjaxResult loginByOpenId(WxLoginDto dto) {
                return AjaxResult.error("登录失败");
            }
        };
    }


}
