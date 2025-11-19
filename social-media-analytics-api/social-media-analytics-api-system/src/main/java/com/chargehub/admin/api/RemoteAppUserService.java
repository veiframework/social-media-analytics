package com.chargehub.admin.api;


import com.chargehub.admin.api.domain.dto.WxLoginDto;
import com.chargehub.admin.api.factory.RemoteAppUserServiceFallbackFactory;
import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.common.core.web.domain.AjaxResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Zhanghaowei
 * @date 2025/08/02 17:49
 */
@FeignClient(contextId = "RemoteAppUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteAppUserServiceFallbackFactory.class)
public interface RemoteAppUserService {


    /**
     * 根据微信openId查询信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/app-user/api/wechat")
    AjaxResult loginByOpenId(@RequestBody WxLoginDto dto);

}
