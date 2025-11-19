package com.chargehub.common.log.service.impl;

import com.chargehub.common.core.constant.SecurityConstants;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.log.controller.SysOperlogController;
import com.chargehub.common.log.service.ISysOperLogService;
import com.chargehub.common.security.service.AsyncLogService;
import com.chargehub.log.api.RemoteLogService;
import com.chargehub.log.api.domain.SysOperLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步调用日志服务
 *
 * @author ruoyi
 */
@Slf4j
@Service
public class AsyncLogServiceImpl implements AsyncLogService {


    @Autowired
    private ISysOperLogService sysOperLogService;

    public AsyncLogServiceImpl() {
        log.info("log module has been found, use local log service... ");
    }

    /**
     * 保存系统日志记录
     */
    @Override
    @Async
    public void saveSysLog(SysOperLog sysOperLog) throws Exception {
        sysOperLogService.insertOperlog(sysOperLog);
    }
}
