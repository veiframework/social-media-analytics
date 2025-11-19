package com.chargehub.job.api;

import com.chargehub.common.core.constant.ServiceNameConstants;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.job.api.domain.dto.SysJobDto;
import com.chargehub.job.api.factory.RemoteSysJobServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Zhanghaowei
 * @date 2024/04/12 18:37
 */
@FeignClient(contextId = "RemoteSysJobService", value = ServiceNameConstants.REMOTE_JOB, fallbackFactory = RemoteSysJobServiceFallbackFactory.class)
public interface RemoteSysJobService {

    /**
     * 添加定时任务
     *
     * @param job
     * @return
     */
    @PostMapping("/job/remote")
    AjaxResult add(@RequestBody SysJobDto job);


}
