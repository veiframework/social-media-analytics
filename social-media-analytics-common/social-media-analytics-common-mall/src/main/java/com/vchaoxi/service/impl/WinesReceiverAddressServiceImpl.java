package com.vchaoxi.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vchaoxi.entity.WinesReceiverAddress;
import com.vchaoxi.mapper.WinesReceiverAddressMapper;
import com.vchaoxi.service.IWinesReceiverAddressService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
@Service
public class WinesReceiverAddressServiceImpl extends ServiceImpl<WinesReceiverAddressMapper, WinesReceiverAddress> implements IWinesReceiverAddressService {



    @Override
    public WinesReceiverAddress getDefaultAddress(Integer userId) {
        List<WinesReceiverAddress> winesReceiverAddressList = this.lambdaQuery()
                .eq(WinesReceiverAddress::getIsDelete,0)
                .eq(WinesReceiverAddress::getUserId,userId)
                .eq(WinesReceiverAddress::getIsDefault,1)
                .list();
        if (CollectionUtils.isEmpty(winesReceiverAddressList)){
            return null;
        }

        return winesReceiverAddressList.get(0);
    }
}
