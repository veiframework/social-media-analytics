package com.vchaoxi.controller.app;


import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.entity.WinesReceiverAddress;
import com.vchaoxi.param.ReceiverAddressParam;
import com.vchaoxi.service.IVcUserUserService;
import com.vchaoxi.service.IWinesReceiverAddressService;
import com.vchaoxi.vo.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
@RestController
@RequestMapping("/app/wines-receiver-address")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WinesReceiverAddressAppController extends BaseController {
    @Autowired
    private IWinesReceiverAddressService winesReceiverAddressService;

    @Autowired
    private IVcUserUserService vcUserUserService;
    
    @GetMapping("/list")
    @RequiresLogin(doIntercept = false)
    public CommonResult list() {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        List<WinesReceiverAddress> receiverAddressList = winesReceiverAddressService.lambdaQuery()
                .eq(WinesReceiverAddress::getIsDelete,0)
                .eq(WinesReceiverAddress::getUserId,vcUserUser.getId()).list();
        return CommonResult.success(receiverAddressList);
    }

    @GetMapping("/default-address")
    @RequiresLogin(doIntercept = false)
    public CommonResult defaultAddress() {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        WinesReceiverAddress address = winesReceiverAddressService.getDefaultAddress(vcUserUser.getId());
        Map<String,Object> map = new HashMap<>();
        map.put("defaultAddredd", address);
        return CommonResult.success(map);
    }


    @PostMapping("/add")
    @RequiresLogin(doIntercept = false)
    public CommonResult add(@RequestBody @Validated({ReceiverAddressParam.Add.class}) ReceiverAddressParam carParam) {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        WinesReceiverAddress winesReceiverAddress = new WinesReceiverAddress();
        BeanUtils.copyProperties(carParam,winesReceiverAddress);
        winesReceiverAddress.setUserId(vcUserUser.getId());
        winesReceiverAddressService.save(winesReceiverAddress);
        List<WinesReceiverAddress> receiverAddressList = winesReceiverAddressService.lambdaQuery()
                .eq(WinesReceiverAddress::getIsDelete,0)
                .eq(WinesReceiverAddress::getUserId,vcUserUser.getId()).list();
        if (receiverAddressList.size() == 1) {
            WinesReceiverAddress receiverAddress = receiverAddressList.get(0);
            receiverAddress.setIsDefault(1);
            winesReceiverAddressService.updateById(receiverAddress);
        }else{
            if (winesReceiverAddress.getIsDefault() == 1) {
                List<WinesReceiverAddress> winesReceiverAddressList = winesReceiverAddressService.lambdaQuery()
                        .eq(WinesReceiverAddress::getIsDelete,0)
                        .eq(WinesReceiverAddress::getUserId,vcUserUser.getId())
                        .eq(WinesReceiverAddress::getIsDefault,1)
                        .ne(WinesReceiverAddress::getId,winesReceiverAddress.getId())
                        .list();
                //如果存在别的默认地址，将其他默认地址修改成非默认地址
                if (!CollectionUtils.isEmpty(winesReceiverAddressList)) {
                    winesReceiverAddressService.lambdaUpdate()
                            .eq(WinesReceiverAddress::getIsDelete,0)
                            .eq(WinesReceiverAddress::getUserId,vcUserUser.getId())
                            .eq(WinesReceiverAddress::getIsDefault,1)
                            .ne(WinesReceiverAddress::getId,winesReceiverAddress.getId())
                            .set(WinesReceiverAddress::getIsDefault,0).update();
                }
            }
        }
        return CommonResult.success(receiverAddressList);
    }

    @PostMapping("/edit")
    @RequiresLogin(doIntercept = false)
    public CommonResult edit(@RequestBody @Validated({ReceiverAddressParam.Edit.class}) ReceiverAddressParam carParam) {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        WinesReceiverAddress winesReceiverAddress = winesReceiverAddressService.getById(carParam.getId());
        if (winesReceiverAddress == null || winesReceiverAddress.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST,"该地址不存在");
        }
        if (!winesReceiverAddress.getUserId().equals(vcUserUser.getId())) {
            return CommonResult.error(HttpStatus.BAD_REQUEST,"无该地址操作权限");
        }
        BeanUtils.copyProperties(carParam,winesReceiverAddress);
        winesReceiverAddressService.updateById(winesReceiverAddress);
        if (winesReceiverAddress.getIsDefault() == 1) {
            List<WinesReceiverAddress> winesReceiverAddressList = winesReceiverAddressService.lambdaQuery()
                    .eq(WinesReceiverAddress::getIsDelete,0)
                    .eq(WinesReceiverAddress::getUserId,vcUserUser.getId())
                    .eq(WinesReceiverAddress::getIsDefault,1)
                    .ne(WinesReceiverAddress::getId,winesReceiverAddress.getId())
                    .list();
            //如果存在别的默认地址，将其他默认地址修改成非默认地址
            if (!CollectionUtils.isEmpty(winesReceiverAddressList)) {
                winesReceiverAddressService.lambdaUpdate()
                        .eq(WinesReceiverAddress::getIsDelete,0)
                        .eq(WinesReceiverAddress::getUserId,vcUserUser.getId())
                        .eq(WinesReceiverAddress::getIsDefault,1)
                        .ne(WinesReceiverAddress::getId,winesReceiverAddress.getId())
                        .set(WinesReceiverAddress::getIsDefault,0).update();
            }
        }
        List<WinesReceiverAddress> receiverAddressList = winesReceiverAddressService.lambdaQuery()
                .eq(WinesReceiverAddress::getIsDelete,0)
                .eq(WinesReceiverAddress::getUserId,vcUserUser.getId()).list();
        return CommonResult.success(receiverAddressList);
    }


    @PostMapping("/del")
    @RequiresLogin(doIntercept = false)
    public CommonResult del(@RequestBody @Validated({ReceiverAddressParam.Del.class}) ReceiverAddressParam carParam) {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        WinesReceiverAddress winesReceiverAddress = winesReceiverAddressService.getById(carParam.getId());
        if (winesReceiverAddress == null || winesReceiverAddress.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST,"该地址不存在");
        }
        if (!winesReceiverAddress.getUserId().equals(vcUserUser.getId())) {
            return CommonResult.error(HttpStatus.BAD_REQUEST,"无该地址操作权限");
        }

        winesReceiverAddress.setIsDelete(1);
        winesReceiverAddressService.updateById(winesReceiverAddress);
        List<WinesReceiverAddress> receiverAddressList = winesReceiverAddressService.lambdaQuery()
                .eq(WinesReceiverAddress::getIsDelete,0)
                .eq(WinesReceiverAddress::getUserId,vcUserUser.getId()).list();
        return CommonResult.success(receiverAddressList);
    }

    @PostMapping("/set-default")
    @RequiresLogin(doIntercept = false)
    @Transactional
    public CommonResult setDefault(@RequestBody @Validated({ReceiverAddressParam.SetDefault.class}) ReceiverAddressParam carParam) {
        VcUserUser vcUserUser = vcUserUserService.getCurrentUser();

        WinesReceiverAddress winesReceiverAddress = winesReceiverAddressService.getById(carParam.getId());
        if (winesReceiverAddress == null || winesReceiverAddress.getIsDelete() != 0) {
            return CommonResult.error(HttpStatus.BAD_REQUEST,"该地址不存在");
        }
        if (!winesReceiverAddress.getUserId().equals(vcUserUser.getId())) {
            return CommonResult.error(HttpStatus.BAD_REQUEST,"无该地址操作权限");
        }
        winesReceiverAddress.setIsDefault(1);
        winesReceiverAddressService.updateById(winesReceiverAddress);
        winesReceiverAddressService.lambdaUpdate()
                .eq(WinesReceiverAddress::getIsDelete,0)
                .eq(WinesReceiverAddress::getUserId,vcUserUser.getId())
                .eq(WinesReceiverAddress::getIsDefault,1)
                .ne(WinesReceiverAddress::getId,winesReceiverAddress.getId())
                .set(WinesReceiverAddress::getIsDefault,0).update();

        List<WinesReceiverAddress> receiverAddressList = winesReceiverAddressService.lambdaQuery()
                .eq(WinesReceiverAddress::getIsDelete,0)
                .eq(WinesReceiverAddress::getUserId,vcUserUser.getId()).list();
        return CommonResult.success(receiverAddressList);
    }

}
