package com.vchaoxi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vchaoxi.entity.WinesReceiverAddress;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
public interface IWinesReceiverAddressService extends IService<WinesReceiverAddress> {
    public WinesReceiverAddress getDefaultAddress(Integer userId);
}
