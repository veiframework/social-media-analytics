package com.chargehub.admin.alarm.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.lang.Assert;
import com.chargehub.admin.alarm.domain.SocialMediaWorkAlarm;
import com.chargehub.admin.alarm.dto.SocialMediaWorkAlarmDto;
import com.chargehub.admin.alarm.dto.SocialMediaWorkAlarmQueryDto;
import com.chargehub.admin.alarm.mapper.SocialMediaWorkAlarmMapper;
import com.chargehub.admin.alarm.vo.SocialMediaWorkAlarmVo;
import com.chargehub.admin.enums.AlarmTypeEnum;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.event.Z9CreateEvent;
import com.chargehub.common.security.template.event.Z9UpdateEvent;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.chargehub.job.api.RemoteSysJobService;
import com.chargehub.job.api.domain.dto.SysJobDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SocialMediaWorkAlarmService extends AbstractZ9CrudServiceImpl<SocialMediaWorkAlarmMapper, SocialMediaWorkAlarm> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    @Autowired
    private RemoteSysJobService remoteSysJobService;

    protected SocialMediaWorkAlarmService(SocialMediaWorkAlarmMapper baseMapper) {
        super(baseMapper);
    }


    @Order
    @EventListener
    public void afterCreateBillingTask(Z9CreateEvent<SocialMediaWorkAlarm> event) {
        List<SocialMediaWorkAlarm> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        addJob(data);
    }

    private void addJob(List<SocialMediaWorkAlarm> data) {
        for (SocialMediaWorkAlarm e : data) {
            String taskId = e.getId();
            AlarmTypeEnum alarmTypeEnum = AlarmTypeEnum.getByDesc(e.getType());
            SysJobDto jobDto = new SysJobDto();
            jobDto.setCreateBy(e.getCreator());
            jobDto.setJobName(e.getAlarmName() + ":" + taskId);
            jobDto.setInvokeTarget(alarmTypeEnum.getBeanName() + ".execute('" + taskId + "')");
            jobDto.setCronExpression(e.getCronExpression());
            jobDto.setStatus(e.getState());
            jobDto.setMisfirePolicy("3");
            AjaxResult result = remoteSysJobService.add(jobDto);
            Assert.isTrue(result.isSuccess(), "创建监控器失败");
        }
    }

    @Override
    public void deleteByIds(String ids) {
        String[] split = ids.split(",");
        for (String id : split) {
            SocialMediaWorkAlarm e = this.baseMapper.doGetDetailById(id);
            AlarmTypeEnum alarmTypeEnum = AlarmTypeEnum.getByDesc(e.getType());
            String taskId = e.getId();
            SysJobDto jobDto = new SysJobDto();
            jobDto.setJobName(e.getAlarmName() + ":" + taskId);
            jobDto.setInvokeTarget(alarmTypeEnum.getBeanName() + ".execute('" + taskId + "')");
            jobDto.setCronExpression(e.getCronExpression());
            jobDto.setStatus("1");
            jobDto.setMisfirePolicy("3");
            AjaxResult result = remoteSysJobService.add(jobDto);
            Assert.isTrue(result.isSuccess(), "暂停监控器失败");
        }
        super.deleteByIds(ids);
    }

    @Order
    @EventListener
    public void afterUpdateBillingTask(Z9UpdateEvent<SocialMediaWorkAlarm> event) {
        List<SocialMediaWorkAlarm> data = event.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        addJob(data);
    }


    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<? extends Z9CrudQueryDto<SocialMediaWorkAlarm>> queryDto() {
        return SocialMediaWorkAlarmQueryDto.class;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return SocialMediaWorkAlarmDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return SocialMediaWorkAlarmVo.class;
    }

    @Override
    public String excelName() {
        return "社交媒体告警器";
    }
}
