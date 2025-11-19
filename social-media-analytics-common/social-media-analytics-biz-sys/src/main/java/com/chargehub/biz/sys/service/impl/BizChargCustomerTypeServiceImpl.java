package com.chargehub.biz.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chargehub.biz.sys.domain.ChargCustomerType;
import com.chargehub.biz.sys.mapper.BizChargCustomerTypeMapper;
import com.chargehub.biz.sys.service.IBizChargCustomerTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/9 14:05
 * @Project：chargehub
 * @Package：com.chargehub.biz.sys.service.impl
 * @Filename：BizSysUserPostServiceImpl
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BizChargCustomerTypeServiceImpl extends ServiceImpl<BizChargCustomerTypeMapper, ChargCustomerType> implements IBizChargCustomerTypeService {
}
