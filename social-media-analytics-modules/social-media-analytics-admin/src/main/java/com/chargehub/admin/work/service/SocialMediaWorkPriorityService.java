package com.chargehub.admin.work.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.chargehub.admin.scheduler.WorkAlarmIntervalScheduler;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.domain.SocialMediaWorkPriority;
import com.chargehub.admin.work.dto.SocialMediaWorkPriorityDto;
import com.chargehub.admin.work.mapper.SocialMediaWorkPriorityMapper;
import com.chargehub.admin.work.vo.SocialMediaWorkPriorityVo;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SocialMediaWorkPriorityService extends AbstractZ9CrudServiceImpl<SocialMediaWorkPriorityMapper, SocialMediaWorkPriority> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    protected SocialMediaWorkPriorityService(SocialMediaWorkPriorityMapper baseMapper) {
        super(baseMapper);
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return SocialMediaWorkPriorityDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return SocialMediaWorkPriorityVo.class;
    }

    public Map<Integer, SocialMediaWorkPriority> getAllPriority() {
        List<SocialMediaWorkPriority> list = baseMapper.doGetAll(null);
        Map<Integer, SocialMediaWorkPriority> map = new TreeMap<>();
        for (SocialMediaWorkPriority priority : list) {
            map.put(priority.getPriority(), priority);
        }
        return map;
    }

    public static Integer computePriority(LocalDateTime localDateTime, SocialMediaWork existWork, SocialMediaWork socialMediaWork, Map<Integer, SocialMediaWorkPriority> allPriority) {
        Date updateTime = existWork.getUpdateTime();
        DateTime now = DateUtil.date(localDateTime);
        for (SocialMediaWorkPriority priority : allPriority.values()) {
            String expression = priority.getPriorityExpression();
            long timeDiffSeconds = DateUtil.between(updateTime, now, DateUnit.SECOND);
            Map<String, Object> map = BeanUtil.beanToMap(socialMediaWork);
            map.put("timeDiffSeconds", timeDiffSeconds);
            StandardEvaluationContext evaluationContext = WorkAlarmIntervalScheduler.createEvaluationContext(map);
            Boolean evalResult = PARSER.parseExpression(expression).getValue(evaluationContext, Boolean.class);
            if (BooleanUtils.isTrue(evalResult)) {
                return priority.getPriority();
            }
        }
        throw new IllegalArgumentException("未找匹配的作品优先级!");
    }

    @Override
    public String excelName() {
        return "作品优先级列表";
    }


}
