package com.chargehub.common.security.service;

import com.chargehub.log.api.domain.SysOperLog;

/**
 * @author Zhanghaowei
 * @date 2025/08/19 18:58
 */
public interface AsyncLogService {

    /**
     * 保存日志
     *
     * @param sysOperLog
     * @throws Exception
     */
    void saveSysLog(SysOperLog sysOperLog) throws Exception;

}
