package com.chargehub.admin.alarm.mapper;

import com.chargehub.admin.alarm.domain.AlarmWebhook;
import com.chargehub.common.security.template.mybatis.Z9MpCrudMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Mapper
public interface AlarmWebhookMapper extends Z9MpCrudMapper<AlarmWebhook> {
}
