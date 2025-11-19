package com.vchaoxi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vchaoxi.entity.Invoice;
import com.vchaoxi.mapper.InvoiceMapper;
import com.vchaoxi.service.IInvoiceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 发票管理 服务实现类
 * </p>
 *
 * @author hanfuxian
 * @since 2024-11-14
 */
@Service
public class InvoiceServiceImpl extends ServiceImpl<InvoiceMapper, Invoice> implements IInvoiceService {

}
