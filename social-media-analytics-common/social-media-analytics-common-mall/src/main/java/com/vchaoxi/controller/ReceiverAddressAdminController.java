package com.vchaoxi.controller;

import com.chargehub.common.security.annotation.RequiresLogin;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.entity.WinesReceiverAddress;
import com.vchaoxi.service.IWinesReceiverAddressService;
import com.vchaoxi.vo.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2025/09/02 11:52
 */
@RestController
@RequestMapping("/receiver-address")
public class ReceiverAddressAdminController {

    @Autowired
    private IWinesReceiverAddressService winesReceiverAddressService;

    @GetMapping("/list")
    @RequiresLogin(doIntercept = false)
    public CommonResult list(String userId) {
        List<WinesReceiverAddress> receiverAddressList = winesReceiverAddressService.lambdaQuery()
                .eq(WinesReceiverAddress::getIsDelete,0)
                .eq(WinesReceiverAddress::getUserId, userId).list();
        return CommonResult.success(receiverAddressList);
    }



}
