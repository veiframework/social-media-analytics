package com.vchaoxi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vchaoxi.entity.VcOrderOperation;
import com.vchaoxi.mapper.VcOrderOperationMapper;
import com.vchaoxi.service.IVcOrderOperationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单相关 - 订单操作记录表 服务实现类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Service
public class VcOrderOperationServiceImpl extends ServiceImpl<VcOrderOperationMapper, VcOrderOperation> implements IVcOrderOperationService {

}
