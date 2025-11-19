package com.chargehub.biz.agreement.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;

import com.chargehub.biz.agreement.domain.Agreement;
import com.chargehub.biz.agreement.dto.AgreementDto;
import com.chargehub.biz.agreement.mapper.AgreementMapper;
import com.chargehub.biz.agreement.vo.AgreementVo;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhanghaowei
 * @date 2025/08/08 16:42
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AgreementService extends AbstractZ9CrudServiceImpl<AgreementMapper, Agreement> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;


    protected AgreementService(AgreementMapper baseMapper) {
        super(baseMapper);
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return AgreementDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return AgreementVo.class;
    }

    @Override
    public String excelName() {
        return "协议管理";
    }
}
