package com.chargehub.admin.alarm.controller;

import com.chargehub.admin.alarm.dto.AlarmWebhookDto;
import com.chargehub.admin.alarm.dto.AlarmWebhookQueryDto;
import com.chargehub.admin.alarm.dto.SocialMediaWorkAlarmDto;
import com.chargehub.admin.alarm.dto.SocialMediaWorkAlarmQueryDto;
import com.chargehub.admin.alarm.service.AlarmNotificationConfig;
import com.chargehub.admin.alarm.service.AlarmNotificationManager;
import com.chargehub.admin.alarm.service.AlarmWebhookService;
import com.chargehub.admin.alarm.service.SocialMediaWorkAlarmService;
import com.chargehub.admin.alarm.vo.AlarmWebhookVo;
import com.chargehub.admin.alarm.vo.SocialMediaWorkAlarmVo;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.InnerAuth;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.controller.AbstractZ9Controller;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@UnifyResult
@RestController
@RequestMapping("/social-media/work/alarm")
public class SocialMediaWorkAlarmController extends AbstractZ9Controller<SocialMediaWorkAlarmDto, SocialMediaWorkAlarmQueryDto, SocialMediaWorkAlarmVo, SocialMediaWorkAlarmService> {

    @Autowired
    private AlarmWebhookService alarmWebhookService;

    @Autowired
    private AlarmNotificationManager alarmNotificationManager;

    protected SocialMediaWorkAlarmController(SocialMediaWorkAlarmService crudService) {
        super(crudService);
    }


    @RequiresLogin
    @ApiOperation("告警webhook")
    @Operation(summary = "告警webhook")
    @GetMapping("/webhook")
    public AlarmWebhookVo getWebhook(AlarmWebhookQueryDto queryDto) {
        Long userId = SecurityUtils.getUserId();
        queryDto.setUserId(userId + "");
        List<? extends Z9CrudVo> all = this.alarmWebhookService.getAll(queryDto);
        if (CollectionUtils.isEmpty(all)) {
            return null;
        }
        return (AlarmWebhookVo) all.get(0);
    }

    @Debounce
    @RequiresLogin
    @ApiOperation("告警webhook")
    @Operation(summary = "告警webhook")
    @PostMapping("/webhook")
    public void addWebhook(@RequestBody @Validated AlarmWebhookDto dto) {
        String id = dto.getId();
        Long userId = SecurityUtils.getUserId();
        dto.setUserId(userId + "");
        if (StringUtils.isBlank(id)) {
            this.alarmWebhookService.create(dto);
        } else {
            this.alarmWebhookService.edit(dto, id);
        }
    }


    @InnerAuth
    @ApiOperation("测试告警webhook")
    @Operation(summary = "测试告警webhook")
    @PostMapping("/webhook/test")
    public void testAlarm(@RequestBody AlarmNotificationConfig alarmNotificationConfig) {
        alarmNotificationManager.send(alarmNotificationConfig);
    }

}
