package com.chargehub.admin.scheduler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.alarm.domain.SocialMediaWorkAlarm;
import com.chargehub.admin.alarm.service.AlarmNotificationConfig;
import com.chargehub.admin.alarm.service.AlarmNotificationManager;
import com.chargehub.admin.alarm.service.SocialMediaWorkAlarmService;
import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.enums.WorkStateEnum;
import com.chargehub.admin.scheduler.domain.WorkAlarmRecordValue;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.biz.admin.service.ISysUserService;
import com.chargehub.common.redis.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Slf4j
@Component("workAlarmIntervalScheduler")
public class WorkAlarmIntervalScheduler {

    public static final String WORK_ALARM_RECORD_KEY = "work-alarm-record:";

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    @Autowired
    private SocialMediaWorkAlarmService socialMediaWorkAlarmService;


    @Autowired
    private AlarmNotificationManager alarmNotificationManager;


    @Autowired
    private RedisService redisService;

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;


    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public void execute(String taskId) {
        SocialMediaWorkAlarm socialMediaWorkAlarm = socialMediaWorkAlarmService.getBaseMapper().doGetDetailById(taskId);
        if (socialMediaWorkAlarm == null) {
            return;
        }
        LocalDate localDate = LocalDate.now();
        String start = localDate.minusDays(62).format(DatePattern.NORM_DATE_FORMATTER);
        String tomorrow = localDate.plusDays(1).format(DatePattern.NORM_DATE_FORMATTER);
        String keyword = socialMediaWorkAlarm.getKeyword();
        String keywordValue = socialMediaWorkAlarm.getKeywordValue();
        boolean hasMore = true;
        long pageNum = 1;
        while (hasMore) {
            SocialMediaWorkQueryDto socialMediaAccountQueryDto = new SocialMediaWorkQueryDto();
            socialMediaAccountQueryDto.setNumber(pageNum);
            socialMediaAccountQueryDto.setSize(50L);
            socialMediaAccountQueryDto.setStartCreateTime(start);
            socialMediaAccountQueryDto.setEndCreateTime(tomorrow);
            socialMediaAccountQueryDto.setState(Sets.newHashSet(WorkStateEnum.OPEN.getDesc(), WorkStateEnum.PRIVATE.getDesc()));
            socialMediaAccountQueryDto.setSearchCount(false);
            if (StringUtils.isNotBlank(keyword)) {
                ReflectUtil.setFieldValue(socialMediaAccountQueryDto, keyword, keywordValue);
            }
            IPage<SocialMediaWorkVo> page = (IPage<SocialMediaWorkVo>) socialMediaWorkService.getPage(socialMediaAccountQueryDto);
            List<SocialMediaWorkVo> records = page.getRecords();
            hasMore = CollectionUtils.isNotEmpty(records);
            pageNum++;
            for (SocialMediaWorkVo socialMediaWorkVo : records) {
                this.executeAlarmCheck(taskId, socialMediaWorkVo, socialMediaWorkAlarm);
            }
        }
    }

    private void executeAlarmCheck(String taskId,
                                   SocialMediaWorkVo socialMediaWorkVo,
                                   SocialMediaWorkAlarm socialMediaWorkAlarm) {
        String alarmExpression = socialMediaWorkAlarm.getAlarmExpression();
        String alarmField = socialMediaWorkAlarm.getAlarmField();
        List<String> msgFields = socialMediaWorkAlarm.getMsgFields();
        String msgTemplate = socialMediaWorkAlarm.getMsgTemplate();
        String id = socialMediaWorkVo.getId();
        String workAlarmRecordKey = WORK_ALARM_RECORD_KEY + taskId + StringPool.COLON + id;
        Integer currentFieldValue = (Integer) ReflectUtil.getFieldValue(socialMediaWorkVo, alarmField);
        WorkAlarmRecordValue lastRecordValue = redisService.getCacheObject(workAlarmRecordKey);
        Integer startInterval = socialMediaWorkAlarm.getStartInterval();
        if (lastRecordValue == null) {
            redisService.setCacheObject(workAlarmRecordKey, new WorkAlarmRecordValue(currentFieldValue, 0), 65L, TimeUnit.DAYS);
            return;
        }
        int count = lastRecordValue.getCount() + 1;
        String alarmExp = alarmExpression;
        if (count == 1 && startInterval != null) {
            alarmExp = alarmExpression.replaceAll("\\d+", startInterval + "");
        }
        Integer oldValue = lastRecordValue.getValue();
        int diffValue = currentFieldValue - oldValue;
        Map<String, Object> evalContextMap = MapUtil.builder(new HashMap<String, Object>()).put(alarmField, currentFieldValue).build();
        evalContextMap.putAll(BeanUtil.beanToMap(socialMediaWorkAlarm));
        evalContextMap.put("alarmExpression", alarmExp);
        evalContextMap.put("oldValue", oldValue);
        evalContextMap.put("diffValue", diffValue);
        StandardEvaluationContext evaluationContext = createEvaluationContext(evalContextMap);
        Boolean evalResult = PARSER.parseExpression(StringPool.HASH + "diffValue" + alarmExp).getValue(evaluationContext, Boolean.class);
        if (BooleanUtils.isTrue(evalResult)) {
            String userId = socialMediaWorkVo.getUserId();
            List<AlarmNotificationConfig> leadershipWebhook = alarmNotificationManager.getLeadershipWebhook(userId);
            if (CollectionUtils.isNotEmpty(leadershipWebhook)) {
                String json = this.toJson(socialMediaWorkVo);
                evalContextMap.putAll(JSON.parseObject(json));
                long leaderUserId = Long.parseLong(leadershipWebhook.get(0).getUserId());
                SysUser sysUser = iSysUserService.selectUserById(leaderUserId);
                if (sysUser != null) {
                    evalContextMap.put("leaderNickname", sysUser.getNickName());
                }
                String uid = socialMediaAccountService.getUidByAccountId(socialMediaWorkVo.getAccountId());
                evalContextMap.put("socialMediaUid", uid);
            }
            for (AlarmNotificationConfig alarmNotificationConfig : leadershipWebhook) {
                List<String> collect = msgFields.stream().map(i -> String.valueOf(evalContextMap.get(i))).collect(Collectors.toList());
                alarmNotificationConfig.setMsgFields(collect);
                alarmNotificationConfig.setMsgTemplate(msgTemplate);
            }
            alarmNotificationManager.send(leadershipWebhook);
            redisService.setCacheObject(workAlarmRecordKey, new WorkAlarmRecordValue(currentFieldValue, count), 65L, TimeUnit.DAYS);
        }
    }

    public static StandardEvaluationContext createEvaluationContext(Map<String, Object> parameterMap) {
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        parameterMap.forEach(standardEvaluationContext::setVariable);
        return standardEvaluationContext;
    }

    public String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
