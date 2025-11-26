package com.chargehub.admin.scheduler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
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
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.biz.admin.service.ISysUserService;
import com.chargehub.common.redis.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Slf4j
@Component("workAlarmIntervalScheduler")
public class WorkAlarmIntervalScheduler {

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

    public static final String WORK_ALARM_RECORD_KEY = "work-alarm-record:";

    public static final String WORK_ALARM_LAST_EXECUTE_TIME_KEY = "work-alarm-last-execute-time:";

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public void execute(String taskId) {
        StopWatch stopWatch = new StopWatch(taskId);
        try {
            stopWatch.start();
            log.info("作品间隔类型告警任务开始:{}", taskId);
            redisService.lock("lock:work-alarm:" + taskId, locked -> {
                if (BooleanUtils.isFalse(locked)) {
                    return null;
                }
                SocialMediaWorkAlarm socialMediaWorkAlarm = socialMediaWorkAlarmService.getBaseMapper().doGetDetailById(taskId);
                if (socialMediaWorkAlarm == null) {
                    return null;
                }
                String workAlarmLastExecuteTimeKey = WORK_ALARM_LAST_EXECUTE_TIME_KEY + taskId;
                String keyword = socialMediaWorkAlarm.getKeyword();
                String keywordValue = socialMediaWorkAlarm.getKeywordValue();
                String lastDatetime = redisService.getCacheObject(workAlarmLastExecuteTimeKey);
                boolean hasMore = true;
                long pageNum = 1;
                while (hasMore) {
                    SocialMediaWorkQueryDto socialMediaAccountQueryDto = new SocialMediaWorkQueryDto();
                    socialMediaAccountQueryDto.setNumber(pageNum);
                    socialMediaAccountQueryDto.setSize(50L);
                    socialMediaAccountQueryDto.setUpdateTime(lastDatetime);
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
                redisService.setCacheObject(workAlarmLastExecuteTimeKey, DateUtil.now());
                return null;
            });
        } finally {
            stopWatch.stop();
            log.info("作品间隔类型告警任务结束:{}, 花费时间{}秒", taskId, stopWatch.getTotalTimeSeconds());
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
            redisService.setCacheObject(workAlarmRecordKey, new WorkAlarmRecordValue(currentFieldValue, 0));
            return;
        }
        int count = lastRecordValue.getCount() + 1;
        String alarmExp = alarmExpression;
        if (count == 1 && startInterval != null) {
            alarmExp = alarmExpression.replaceAll("\\d+", startInterval + "");
        }
        Map<String, Object> evalContextMap = MapUtil.builder(new HashMap<String, Object>()).put(alarmField, currentFieldValue).build();
        evalContextMap.putAll(BeanUtil.beanToMap(socialMediaWorkAlarm));
        evalContextMap.put("alarmExpression", alarmExp);
        evalContextMap.put("oldValue", lastRecordValue.getValue());
        StandardEvaluationContext evaluationContext = createEvaluationContext(evalContextMap);
        Boolean evalResult = PARSER.parseExpression(StringPool.HASH + alarmField + alarmExp).getValue(evaluationContext, Boolean.class);
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
            redisService.setCacheObject(workAlarmRecordKey, new WorkAlarmRecordValue(currentFieldValue, count));
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
