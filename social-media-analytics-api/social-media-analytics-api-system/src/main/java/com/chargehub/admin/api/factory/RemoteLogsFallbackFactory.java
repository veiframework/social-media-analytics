package com.chargehub.admin.api.factory;

import com.chargehub.admin.api.RemoteLogService;
import com.chargehub.admin.api.domain.SysLogininfor;
import com.chargehub.admin.api.domain.SysOperLog;
import com.chargehub.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 日志服务降级处理
 * 
 * @author ruoyi
 */
@Component
public class RemoteLogsFallbackFactory implements FallbackFactory<RemoteLogService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteLogsFallbackFactory.class);

    @Override
    public RemoteLogService create(Throwable throwable)
    {
        log.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteLogService()
        {
            @Override
            public R<Boolean> saveLog(SysOperLog sysOperLog, String source)
            {
                return null;
            }

            @Override
            public R<Boolean> saveLogininfor(SysLogininfor sysLogininfor, String source)
            {
                return null;
            }
        };

    }
}
