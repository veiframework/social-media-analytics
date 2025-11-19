package com.chargehub.common.security.service;

import com.chargehub.common.core.constant.SecurityConstants;
import com.chargehub.log.api.RemoteLogService;
import com.chargehub.log.api.domain.SysOperLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

/**
 * @author Zhanghaowei
 * @date 2025/08/19 19:00
 */
@Slf4j
public class RemoteAsyncLogServiceImpl implements AsyncLogService {

    private final RemoteLogService remoteLogService;

    public RemoteAsyncLogServiceImpl(RemoteLogService remoteLogService) {
        this.remoteLogService = remoteLogService;
        log.info("not found log module, fallback to remote log service...");
    }

    @Async
    @Override
    public void saveSysLog(SysOperLog sysOperLog) throws Exception {
        remoteLogService.saveLog(sysOperLog, SecurityConstants.INNER);
    }
}
