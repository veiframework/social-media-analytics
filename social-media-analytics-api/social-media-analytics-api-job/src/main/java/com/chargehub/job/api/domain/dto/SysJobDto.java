package com.chargehub.job.api.domain.dto;

import cn.hutool.core.date.DateUtil;
import cn.hutool.cron.pattern.CronPatternBuilder;
import cn.hutool.cron.pattern.Part;
import com.chargehub.common.core.annotation.Excel;
import com.chargehub.common.core.constant.ScheduleConstants;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2024/04/12 18:43
 */
@Data
public class SysJobDto implements Serializable {

    private static final long serialVersionUID = -8043559391703055855L;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    private String jobName;

    /**
     * 任务组名
     */
    @Excel(name = "任务组名")
    private String jobGroup = "DEFAULT";

    /**
     * 调用目标字符串
     */
    @Excel(name = "调用目标字符串")
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @Excel(name = "执行表达式 ")
    private String cronExpression;

    /**
     * cron计划策略
     */
    @Excel(name = "计划策略 ", readConverterExp = "0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行")
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @Excel(name = "并发执行", readConverterExp = "0=允许,1=禁止")
    private String concurrent = "1";

    /**
     * 任务状态（0正常 1暂停）
     */
    @Excel(name = "任务状态", readConverterExp = "0=正常,1=暂停")
    private String status = "0";


    /**
     * 创建者
     */
    private String createBy;


    public void buildCronByDate(Date date) {
        LocalDateTime localDateTime = DateUtil.toLocalDateTime(date);
        int year = localDateTime.getYear();
        int monthValue = localDateTime.getMonthValue();
        int dayOfMonth = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second = localDateTime.getSecond();
        this.cronExpression = CronPatternBuilder.of()
                .set(Part.SECOND, second + "")
                .set(Part.MINUTE, minute + "")
                .set(Part.HOUR, hour + "")
                .set(Part.DAY_OF_MONTH, dayOfMonth + "")
                .set(Part.MONTH, monthValue + "")
                .set(Part.DAY_OF_WEEK, "?")
                .set(Part.YEAR, year + "")
                .build();
    }
}
