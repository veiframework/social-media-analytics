package com.chargehub.admin.alarm.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import com.chargehub.admin.alarm.domain.AlarmWebhook;
import com.chargehub.admin.alarm.dto.AlarmWebhookDto;
import com.chargehub.admin.alarm.mapper.AlarmWebhookMapper;
import com.chargehub.admin.alarm.vo.AlarmWebhookVo;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AlarmWebhookService extends AbstractZ9CrudServiceImpl<AlarmWebhookMapper, AlarmWebhook> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;

    protected AlarmWebhookService(AlarmWebhookMapper baseMapper) {
        super(baseMapper);
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }


    @Override
    public Class<?> doGetDtoClass() {
        return AlarmWebhookDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return AlarmWebhookVo.class;
    }

    @Override
    public String excelName() {
        return "告警webhook管理";
    }
}
