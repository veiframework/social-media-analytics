package com.vchaoxi.service.impl;

import cn.afterturn.easypoi.excel.entity.result.ExcelVerifyHandlerResult;
import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import com.chargehub.common.security.service.ChargeExcelDictHandler;
import com.chargehub.common.security.template.mapper.Z9BaseCrud;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.vchaoxi.entity.MemberOpenOption;
import com.vchaoxi.mapper.MemberOpenOptionMapper;
import com.vchaoxi.param.MemberOpenOptionDto;
import com.vchaoxi.vo.MemberOpenOptionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 16:05
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberOpenOptionService extends AbstractZ9CrudServiceImpl<MemberOpenOptionMapper, MemberOpenOption> {

    @Autowired
    private ChargeExcelDictHandler chargeExcelDictHandler;


    protected MemberOpenOptionService(MemberOpenOptionMapper baseMapper) {
        super(baseMapper);
    }

    @Override
    public IExcelDictHandler getDictHandler() {
        return chargeExcelDictHandler;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return MemberOpenOptionDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return MemberOpenOptionVo.class;
    }


    @Override
    public String excelName() {
        return "会员开通选项";
    }

}
