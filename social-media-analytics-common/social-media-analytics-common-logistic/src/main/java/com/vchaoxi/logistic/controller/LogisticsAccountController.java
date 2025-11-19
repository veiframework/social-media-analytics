package com.vchaoxi.logistic.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.utils.SecurityUtils;
import com.chargehub.thirdparty.api.RemoteWxLogisticsService;
import com.chargehub.thirdparty.api.domain.dto.logistics.LoAccountBindParam;
import com.chargehub.thirdparty.api.domain.dto.logistics.LoQuotaGetParam;
import com.chargehub.thirdparty.api.domain.vo.logistics.LoAccountBindVo;
import com.chargehub.thirdparty.api.domain.vo.logistics.LoQuotaGetVo;
import com.vchaoxi.logistic.domain.ShopRelativeId;
import com.vchaoxi.logistic.domain.VcLogisticsAccount;
import com.vchaoxi.logistic.domain.VcLogisticsDelivery;
import com.vchaoxi.logistic.mapper.VcLogisticsAccountMapper;
import com.vchaoxi.logistic.mapper.VcLogisticsDeliveryMapper;
import com.vchaoxi.logistic.param.LogisticsAccountParam;
import com.vchaoxi.logistic.service.IVcLogisticsAccountService;
import com.vchaoxi.logistic.service.IVcLogisticsDeliveryService;
import com.vchaoxi.logistic.service.ShopExtendService;
import com.vchaoxi.logistic.vo.LogisticsAccountVo;
import com.vchaoxi.logistic.vo.LogisticsDeliveryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 物流账号相关接口
 */
@RestController
@RequestMapping("/logistics-account")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogisticsAccountController extends BaseController {


    @Autowired
    private IVcLogisticsAccountService vcLogisticsAccountService;
    @Autowired
    private RemoteWxLogisticsService wxOpenLogisticsApiClient;
    @Autowired
    private ShopExtendService shopExtendService;
    @Autowired
    private IVcLogisticsDeliveryService vcLogisticsDeliveryService;


    private final VcLogisticsAccountMapper vcLogisticsAccountMapper;
    private final VcLogisticsDeliveryMapper vcLogisticsDeliveryMapper;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 获取支持的快递公司列表
     *
     * @return
     */
    @GetMapping("/all-delivery")
    @RequiresLogin(doIntercept = false)
    @Transactional
    public AjaxResult allDelivery() {
        List<LogisticsDeliveryVo> list = vcLogisticsDeliveryMapper.selectDelivery();
        for (LogisticsDeliveryVo logisticsDeliveryVo : list) {
            logisticsDeliveryVo.setServiceType(JSONObject.parseArray(logisticsDeliveryVo.getServiceType().toString()));
        }
        return AjaxResult.success(list);
    }


    /**
     * 获取物流配送公司支持的服务类型
     *
     * @param deliveryId
     * @return
     */
    @GetMapping("/delivery-service-type")
    @Transactional
    public AjaxResult deliveryServiceType(@RequestParam("deliveryId") String deliveryId) {
        VcLogisticsDelivery vcLogisticsDelivery = vcLogisticsDeliveryService.lambdaQuery().eq(VcLogisticsDelivery::getIsDelete, 0)
                .eq(VcLogisticsDelivery::getDeliveryId, deliveryId).one();
        if (vcLogisticsDelivery == null) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "不存在的物流配送公司");
        }
        return AjaxResult.success(JSONObject.parseArray(vcLogisticsDelivery.getServiceType()));
    }


    /**
     * 绑定物流账号
     *
     * @param logisticsAccountParam
     * @return
     */
    @Debounce
    @PostMapping("/bind")
    @RequiresPermissions({"operateData:logistics:accoAndAddr:accoBind"})
    @Transactional
    public AjaxResult bind(@RequestBody @Validated({LogisticsAccountParam.Bind.class}) LogisticsAccountParam logisticsAccountParam) {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        logisticsAccountParam.setShopId(loginUser.getShopId());

        //判断当前账号在该商家下是否已经存在
        Long count = vcLogisticsAccountService.lambdaQuery().eq(VcLogisticsAccount::getIsDelete, 0).eq(VcLogisticsAccount::getStatus, 1)
                .eq(VcLogisticsAccount::getShopId, logisticsAccountParam.getShopId()).eq(VcLogisticsAccount::getBizId, logisticsAccountParam.getBizId()).count();
        if (count > 0) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "该账号已经绑定");
        }

        ShopRelativeId shopRelativeId = shopExtendService.getMaAppId(logisticsAccountParam.getShopId());
        String maAppId = shopRelativeId.getMaAppId();
        //请求微信api 进行绑定操作
        LoAccountBindParam loAccountBindParam = new LoAccountBindParam();
        loAccountBindParam.setType("bind");
        loAccountBindParam.setBiz_id(logisticsAccountParam.getBizId());
        loAccountBindParam.setDelivery_id(logisticsAccountParam.getDeliveryId());
        loAccountBindParam.setPassword(logisticsAccountParam.getPassword());
        loAccountBindParam.setRemark_content(logisticsAccountParam.getRemarkContent());
        LoAccountBindVo loAccountBindVo = wxOpenLogisticsApiClient.accountBind(maAppId, loAccountBindParam);
        if (loAccountBindVo == null || (loAccountBindVo.getErrcode() != 0 && loAccountBindVo.getErrcode() != 9300529)) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "绑定物流账号失败，微信返回信息：" + loAccountBindVo.getErrmsg());
        }

        //查询物流账号余额信息
        LoQuotaGetParam loQuotaGetParam = new LoQuotaGetParam();
        loQuotaGetParam.setDelivery_id(logisticsAccountParam.getDeliveryId());
        loQuotaGetParam.setBiz_id(logisticsAccountParam.getBizId());
        LoQuotaGetVo loQuotaGetVo = wxOpenLogisticsApiClient.quotaGet(maAppId, loQuotaGetParam);

        //添加物流信息
        VcLogisticsAccount vcLogisticsAccount = new VcLogisticsAccount();
        vcLogisticsAccount.setAgentId(shopRelativeId.getAgentId());
        vcLogisticsAccount.setShopId(loginUser.getShopId());
        vcLogisticsAccount.setDeliveryId(logisticsAccountParam.getDeliveryId());
        vcLogisticsAccount.setDeliveryName(logisticsAccountParam.getDeliveryName());
        vcLogisticsAccount.setBizId(logisticsAccountParam.getBizId());
        vcLogisticsAccount.setPassword(logisticsAccountParam.getPassword());
        vcLogisticsAccount.setRemarkContent(logisticsAccountParam.getRemarkContent());
        vcLogisticsAccount.setStatus(1);
        if (loQuotaGetVo != null && loQuotaGetVo.getErrcode() == 0) {
            vcLogisticsAccount.setQuotaNum(loQuotaGetVo.getQuota_num());
            vcLogisticsAccount.setQuotaUpdateTime(LocalDateTime.now());
        }
        vcLogisticsAccountService.save(vcLogisticsAccount);
        return AjaxResult.success();
    }


    /**
     * 解绑账号操作
     *
     * @param logisticsAccountParam
     * @return
     */
    @Debounce
    @PostMapping("/unbind")
    @RequiresPermissions({"operateData:logistics:accoAndAddr:accoUnbind"})
    @Transactional
    public AjaxResult unbind(@RequestBody @Validated({LogisticsAccountParam.Unbind.class}) LogisticsAccountParam logisticsAccountParam) {


        //查询物流账号信息
        VcLogisticsAccount vcLogisticsAccount = vcLogisticsAccountService.lambdaQuery().eq(VcLogisticsAccount::getId, logisticsAccountParam.getId()).one();
        if (vcLogisticsAccount == null || vcLogisticsAccount.getIsDelete() != 0) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "该物流账号不存在");
        }

        //直接进行解绑操作
        ShopRelativeId shopRelativeId = shopExtendService.getMaAppId(vcLogisticsAccount.getShopId());
        String maAppId = shopRelativeId.getMaAppId();

        //请求微信api 进行绑定操作
        LoAccountBindParam loAccountBindParam = new LoAccountBindParam();
        loAccountBindParam.setType("unbind");
        loAccountBindParam.setBiz_id(vcLogisticsAccount.getBizId());
        loAccountBindParam.setDelivery_id(vcLogisticsAccount.getDeliveryId());
        loAccountBindParam.setPassword(vcLogisticsAccount.getPassword());
        loAccountBindParam.setRemark_content(vcLogisticsAccount.getRemarkContent());
        LoAccountBindVo loAccountBindVo = wxOpenLogisticsApiClient.accountBind(maAppId, loAccountBindParam);
        if (loAccountBindVo == null || loAccountBindVo.getErrcode() != 0) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "解绑物流账号失败，微信返回信息：" + loAccountBindVo.getErrmsg());
        }
        //修改系统内账号记录状态为解绑状态
        vcLogisticsAccountService.lambdaUpdate().eq(VcLogisticsAccount::getId, vcLogisticsAccount.getId())
                .set(VcLogisticsAccount::getStatus, 0).update();
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
    @RequiresPermissions({"operateData:logistics:accoAndAddr:accoList"})
    @Transactional
    public AjaxResult list(@RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum, @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize,
                           @RequestParam(defaultValue = "", value = "shopId") Integer shopId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }
        ShopRelativeId shopRelativeId = shopExtendService.getMaAppId(shopId);

        Page<LogisticsAccountVo> page = new Page<>(pageNum, pageSize);
        List<LogisticsAccountVo> list = vcLogisticsAccountMapper.adminSelectList(page, shopRelativeId.getAgentId(), shopId);
        return AjaxResult.success(page.setRecords(list));
    }


    /**
     * 查询商家所有账号
     *
     * @param shopId
     * @return
     */
    @GetMapping("/all-account")
    @RequiresLogin(doIntercept = false)
    public AjaxResult allAccount(@RequestParam(defaultValue = "", value = "shopId") Integer shopId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (shopId == null) {
            shopId = loginUser.getShopId();
        }
        List<LogisticsAccountVo> list = vcLogisticsAccountMapper.selectByShopId(shopId);
        return AjaxResult.success(list);
    }


}
