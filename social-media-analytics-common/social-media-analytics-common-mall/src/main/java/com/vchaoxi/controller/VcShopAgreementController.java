package com.vchaoxi.controller;


import com.chargehub.common.core.constant.HttpStatus;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.vchaoxi.entity.VcShopAgreement;
import com.vchaoxi.param.VcShopAgreementParam;
import com.vchaoxi.service.IVcShopAgreementService;
import com.vchaoxi.vo.CommonResult;
import com.vchaoxi.vo.VcShopAgreementVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 应用说明 前端控制器
 * </p>
 *
 * @author hanfuxian
 * @since 2024-09-19
 */
@RestController
@RequestMapping("/vc-shop-agreement")
public class VcShopAgreementController {

    @Autowired
    private IVcShopAgreementService shopAgreementService;

    /**
     * 查看说明
     *
     * @return
     */
    @GetMapping("/one")
    @RequiresPermissions({"systemManage:info:agreement:one"})
    public CommonResult one() {
        //返回 id 10 的说明记录
        VcShopAgreement shopAgreement = shopAgreementService.lambdaQuery()
                .eq(VcShopAgreement::getIsDelete, 0)
                .eq(VcShopAgreement::getId,10).one();
        VcShopAgreementVo vcShopAgreementVo = new VcShopAgreementVo();
        BeanUtils.copyProperties(shopAgreement, vcShopAgreementVo);
        return CommonResult.success(shopAgreement);
    }

    /**
     * 更新说明
     *
     * @param shopAgreementParam
     * @return
     */
    @PostMapping("/update")
    @RequiresPermissions({"systemManage:info:agreement:update"})
    public CommonResult update(@RequestBody VcShopAgreementParam shopAgreementParam) {
        VcShopAgreement shopAgreementDb = shopAgreementService.lambdaQuery()
                .eq(VcShopAgreement::getIsDelete, 0)
                .eq(VcShopAgreement::getId, 10).one();
        if (Objects.isNull(shopAgreementDb)) {
            return CommonResult.error(HttpStatus.BAD_REQUEST, "说明记录不存在");
        }
        shopAgreementDb.setSignupDetails(shopAgreementParam.getSignupDetails());
        shopAgreementDb.setUpgradeDetails(shopAgreementParam.getUpgradeDetails());
        shopAgreementService.updateById(shopAgreementDb);
        return CommonResult.success();
    }

}
