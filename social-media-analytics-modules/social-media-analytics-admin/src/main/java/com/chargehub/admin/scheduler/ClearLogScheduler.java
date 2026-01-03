package com.chargehub.admin.scheduler;

import cn.hutool.core.date.DatePattern;
import com.chargehub.job.mapper.SysJobLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author Zhanghaowei
 * @since 2026-01-03 11:23
 */
@Component
public class ClearLogScheduler {

    @Autowired
    private SysJobLogMapper sysJobLogMapper;

    public void execute() {
        String localDate = LocalDate.now().format(DatePattern.NORM_DATE_FORMATTER);
        sysJobLogMapper.cleanJobLogByDate(localDate);
    }

}
