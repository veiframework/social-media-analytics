package com.vchaoxi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vchaoxi.entity.VcOrderInfo;
import com.vchaoxi.mapper.VcOrderInfoMapper;
import com.vchaoxi.service.IVcOrderInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单相关 - 订单信息 服务实现类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VcOrderInfoServiceImpl extends ServiceImpl<VcOrderInfoMapper, VcOrderInfo> implements IVcOrderInfoService {

}
