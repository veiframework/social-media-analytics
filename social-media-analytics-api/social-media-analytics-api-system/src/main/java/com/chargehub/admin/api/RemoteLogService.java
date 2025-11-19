package com.chargehub.admin.api;

import com.chargehub.admin.api.domain.SysLogininfor;
import com.chargehub.admin.api.domain.SysOperLog;
import com.chargehub.admin.api.factory.RemoteLogsFallbackFactory;
import com.chargehub.common.core.constant.SecurityConstants;
import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 日志服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "remoteLogServiceOld", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteLogsFallbackFactory.class)
public interface RemoteLogService
{
    /**
     * 保存系统日志
     *
     * @param sysOperLog 日志实体
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/operlog")
    public R<Boolean> saveLog(@RequestBody SysOperLog sysOperLog, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) throws Exception;

    /**
     * 保存访问记录
     *
     * @param sysLogininfor 访问实体
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/logininfor")
    public R<Boolean> saveLogininfor(@RequestBody SysLogininfor sysLogininfor, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
