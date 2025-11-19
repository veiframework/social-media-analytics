package com.chargehub.job.api.factory;

import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.job.api.RemoteSysJobService;
import com.chargehub.job.api.domain.dto.SysJobDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author Zhanghaowei
 * @date 2024/04/13 10:35
 */
@Slf4j
@Component
public class RemoteSysJobServiceFallbackFactory implements FallbackFactory<RemoteSysJobService> {
    @Override
    public RemoteSysJobService create(Throwable cause) {
        log.error("命令执行失败", cause);
        return new RemoteSysJobService() {
            @Override
            public AjaxResult add(SysJobDto job) {
                return AjaxResult.error(cause.getMessage());
            }
        };
    }
}
