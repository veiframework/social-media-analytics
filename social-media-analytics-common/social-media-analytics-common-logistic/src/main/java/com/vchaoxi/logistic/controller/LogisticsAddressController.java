package com.vchaoxi.logistic.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.utils.SecurityUtils;
import com.vchaoxi.logistic.domain.ShopRelativeId;
import com.vchaoxi.logistic.domain.VcLogisticsAddress;
import com.vchaoxi.logistic.mapper.VcLogisticsAddressMapper;
import com.vchaoxi.logistic.param.LogisticsAddressParam;
import com.vchaoxi.logistic.param.LogisticsStatusParam;
import com.vchaoxi.logistic.service.IVcLogisticsAddressService;
import com.vchaoxi.logistic.service.ShopExtendService;
import com.vchaoxi.logistic.vo.LogisticsAddressVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 物流地址相关接口
 */
@RestController
@RequestMapping("/logistics-address")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogisticsAddressController extends BaseController {


    @Autowired
    private IVcLogisticsAddressService vcLogisticsAddressService;


    @Autowired
    private ShopExtendService shopExtendService;

    private final VcLogisticsAddressMapper vcLogisticsAddressMapper;


    /**
     * 添加物流地址信息
     *
     * @param logisticsAddressParam
     * @return
     */
    @PostMapping("/add")
    @RequiresPermissions({"operateData:logistics:accoAndAddr:addrAdd"})
    @Transactional
    public AjaxResult add(@RequestBody @Validated({LogisticsAddressParam.Add.class}) LogisticsAddressParam logisticsAddressParam) {
        //验证相关参数信息
        if (StringUtils.isEmpty(logisticsAddressParam.getTel()) && StringUtils.isEmpty(logisticsAddressParam.getMobile())) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "座机号码和手机号码不能同时为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Integer shopId = loginUser.getShopId();
        logisticsAddressParam.setShopId(shopId);
        ShopRelativeId shopRelativeId = shopExtendService.getMaAppId(shopId);

        //添加地址信息
        VcLogisticsAddress vcLogisticsAddress = new VcLogisticsAddress();
        BeanUtils.copyProperties(logisticsAddressParam, vcLogisticsAddress);
        vcLogisticsAddress.setAgentId(shopRelativeId.getAgentId());
        vcLogisticsAddress.setShopId(shopId);
        vcLogisticsAddressService.save(vcLogisticsAddress);
        //如果当前地址为默认地址，修改默认地址信息
        if (logisticsAddressParam.getIsDefault() == 1) {
            vcLogisticsAddressService.lambdaUpdate().eq(VcLogisticsAddress::getIsDelete, 0).eq(VcLogisticsAddress::getShopId, vcLogisticsAddress.getShopId())
                    .ne(VcLogisticsAddress::getId, vcLogisticsAddress.getId()).set(VcLogisticsAddress::getIsDefault, 0).update();
        }
        return AjaxResult.success();
    }


    /**
     * 编辑物流地址信息
     *
     * @param logisticsAddressParam
     * @return
     */
    @PostMapping("/edit")
    @RequiresPermissions({"operateData:logistics:accoAndAddr:addrEdit"})
    @Transactional
    public AjaxResult edit(@RequestBody @Validated({LogisticsAddressParam.Edit.class}) LogisticsAddressParam logisticsAddressParam) {
        //验证相关参数信息
        if (StringUtils.isEmpty(logisticsAddressParam.getTel()) && StringUtils.isEmpty(logisticsAddressParam.getMobile())) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "座机号码和手机号码不能同时为空");
        }
        //查询物流地址信息
        VcLogisticsAddress vcLogisticsAddress = vcLogisticsAddressService.lambdaQuery().eq(VcLogisticsAddress::getId, logisticsAddressParam.getId()).one();
        if (vcLogisticsAddress == null || vcLogisticsAddress.getIsDelete() != 0) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "该地址信息不存在");
        }

        //修改地址信息
        vcLogisticsAddressService.lambdaUpdate().eq(VcLogisticsAddress::getId, vcLogisticsAddress.getId())
                .set(VcLogisticsAddress::getName, logisticsAddressParam.getName()).set(VcLogisticsAddress::getTel, logisticsAddressParam.getTel())
                .set(VcLogisticsAddress::getMobile, logisticsAddressParam.getMobile()).set(VcLogisticsAddress::getCompany, logisticsAddressParam.getCompany())
                .set(VcLogisticsAddress::getPostCode, logisticsAddressParam.getPostCode()).set(VcLogisticsAddress::getCountry, logisticsAddressParam.getCountry())
                .set(VcLogisticsAddress::getProvince, logisticsAddressParam.getProvince()).set(VcLogisticsAddress::getCity, logisticsAddressParam.getCity())
                .set(VcLogisticsAddress::getArea, logisticsAddressParam.getArea()).set(VcLogisticsAddress::getAddress, logisticsAddressParam.getAddress())
                .set(VcLogisticsAddress::getIsDefault, logisticsAddressParam.getIsDefault())
                .set(VcLogisticsAddress::getProvinceCode, logisticsAddressParam.getProvinceCode()).set(VcLogisticsAddress::getCityCode, logisticsAddressParam.getCityCode())
                .set(VcLogisticsAddress::getAreaCode, logisticsAddressParam.getAreaCode()).update();
        //如果当前地址为默认地址，修改默认地址信息
        if (logisticsAddressParam.getIsDefault() == 1) {
            vcLogisticsAddressService.lambdaUpdate().eq(VcLogisticsAddress::getIsDelete, 0).eq(VcLogisticsAddress::getShopId, vcLogisticsAddress.getShopId())
                    .ne(VcLogisticsAddress::getId, vcLogisticsAddress.getId()).set(VcLogisticsAddress::getIsDefault, 0).update();
        }
        return AjaxResult.success();
    }


    /**
     * 编辑物流地址信息
     *
     * @param logisticsAddressParam
     * @return
     */
    @PostMapping("/del")
    @RequiresPermissions({"operateData:logistics:accoAndAddr:addrDel"})
    @Transactional
    public AjaxResult del(@RequestBody @Validated({LogisticsAddressParam.Del.class}) LogisticsAddressParam logisticsAddressParam) {
        //查询物流地址信息
        VcLogisticsAddress vcLogisticsAddress = vcLogisticsAddressService.lambdaQuery().eq(VcLogisticsAddress::getId, logisticsAddressParam.getId()).one();
        if (vcLogisticsAddress == null || vcLogisticsAddress.getIsDelete() != 0) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "该地址信息不存在");
        }


        //修改地址信息
        vcLogisticsAddressService.lambdaUpdate().eq(VcLogisticsAddress::getId, vcLogisticsAddress.getId())
                .set(VcLogisticsAddress::getIsDelete, 1).set(VcLogisticsAddress::getDeleteTime, LocalDateTime.now()).update();
        return AjaxResult.success();
    }


    /**
     * 获取物流账号列表
     *
     * @param pageNum
     * @param pageSize
     * @param shopId
     * @return
     */
    @GetMapping("/list")
    @RequiresPermissions({"operateData:logistics:accoAndAddr:addrList"})
    @Transactional
    public AjaxResult list(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                           @RequestParam(defaultValue = "", value = "shopId") Integer shopId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }
        ShopRelativeId shopRelativeId = shopExtendService.getMaAppId(shopId);

        Page<LogisticsAddressVo> page = new Page<>(pageNum, pageSize);
        List<LogisticsAddressVo> list = vcLogisticsAddressMapper.adminSelectList(page, shopRelativeId.getAgentId(), shopId);
        return AjaxResult.success(page.setRecords(list));
    }


    /**
     * 获取商家开关物流状态
     *
     * @param shopId
     * @return
     */
    @GetMapping("/get-shop-status")
    @RequiresLogin(doIntercept = false)
    public AjaxResult getShopStatus(@RequestParam(defaultValue = "", value = "shopId") Integer shopId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }
        Integer openLogisticsStatus = shopExtendService.getOpenLogisticsStatus(shopId);
        return AjaxResult.success(openLogisticsStatus);
    }


    /**
     * 设置物流开关状态
     *
     * @param logisticsStatusParam
     * @return
     */
    @PostMapping("/set-shop-status")
    @RequiresPermissions({"operateData:logistics:accoAndAddr:setStatus"})
    public AjaxResult setShopStatus(@RequestBody @Validated({LogisticsAddressParam.Del.class}) LogisticsStatusParam logisticsStatusParam) {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        logisticsStatusParam.setShopId(loginUser.getShopId());


        shopExtendService.updateOpenLogistics(logisticsStatusParam.getShopId(), logisticsStatusParam.getStatus());

        return AjaxResult.success();
    }


    /**
     * 获取商家的全部地址信息
     *
     * @param shopId
     * @return
     */
    @GetMapping("/all-addr")
    @RequiresLogin(doIntercept = false)
    public AjaxResult allAddr(@RequestParam(defaultValue = "", value = "shopId") Integer shopId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list", vcLogisticsAddressMapper.selectByShopId(shopId));
        map.put("default", vcLogisticsAddressMapper.selectShopDefault(shopId));
        return AjaxResult.success(map);
    }


}
