package com.vchaoxi.service.impl;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.vchaoxi.entity.MemberOpenRecord;
import com.vchaoxi.mapper.MemberOpenRecordMapper;
import com.vchaoxi.param.MemberOpenRecordDto;
import com.vchaoxi.vo.MemberOpenRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 17:29
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberOpenRecordService extends AbstractZ9CrudServiceImpl<MemberOpenRecordMapper, MemberOpenRecord> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;


    protected MemberOpenRecordService(MemberOpenRecordMapper baseMapper) {
        super(baseMapper);
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return MemberOpenRecordDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return MemberOpenRecordVo.class;
    }

    @Override
    public String excelName() {
        return "会员开通记录";
    }
}
