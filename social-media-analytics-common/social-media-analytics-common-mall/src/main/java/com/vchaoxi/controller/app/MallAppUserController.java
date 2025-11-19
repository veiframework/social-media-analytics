package com.vchaoxi.controller.app;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.common.core.utils.file.ImageUtils;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.utils.SecurityUtils;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vchaoxi.entity.CommissionRecord;
import com.vchaoxi.entity.VcShopShop;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.param.CommissionRecordQueryDto;
import com.vchaoxi.param.CommissionWithdrawDto;
import com.vchaoxi.param.MemberOpenOptionQueryDto;
import com.vchaoxi.service.IVcShopShopService;
import com.vchaoxi.service.IVcShopWxService;
import com.vchaoxi.service.IVcUserUserService;
import com.vchaoxi.service.impl.CommissionRecordService;
import com.vchaoxi.service.impl.MemberOpenOptionService;
import com.vchaoxi.vo.CommissionIncomeVo;
import com.vchaoxi.vo.CommissionRecordVo;
import com.vchaoxi.vo.MemberOpenOptionVo;
import com.vchaoxi.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2025/08/27 17:37
 */
@Api(tags = "商城app用户管理")
@RestController
@RequestMapping("/mall/app-user")
@UnifyResult
public class MallAppUserController {

    @Autowired
    private IVcUserUserService vcUserUserService;

    @Autowired
    private CommissionRecordService commissionRecordService;

    @Autowired
    private MemberOpenOptionService memberOpenOptionService;

    @Autowired
    private IVcShopShopService vcShopShopService;

    @ApiOperation("更新邀请人的登录id")
    @RequiresLogin(doIntercept = false)
    @PostMapping("/invited")
    public void updateInviteLoginId(String inviteLoginId) {
        VcUserUser currentUser = vcUserUserService.getCurrentUser();
        Long loginId = SecurityUtils.getUserId();
        if (inviteLoginId.equals(loginId + "")) {
            //当前用户和登录用户相同则无效
            return;
        }
        this.vcUserUserService.updateInviteLoginId(currentUser.getId(), inviteLoginId);
    }

    @ApiOperation("获取推广的用户列表")
    @RequiresLogin(doIntercept = false)
    @GetMapping("/invited/list")
    public List<UserVo> getInvitedUser() {
        Long userId = SecurityUtils.getUserId();
        return commissionRecordService.getInvitedUser(userId + "");
    }

    @ApiOperation("获取当前用户的提成记录")
    @RequiresLogin(doIntercept = false)
    @GetMapping("/commission/records")
    public IPage<CommissionRecordVo> getCommissionPage(CommissionRecordQueryDto queryDto) {
        Long userId = SecurityUtils.getUserId();
        queryDto.setLoginId(userId + "");
        return (IPage<CommissionRecordVo>) commissionRecordService.getPage(queryDto);
    }

    @ApiOperation("获取提成收益统计")
    @RequiresLogin(doIntercept = false)
    @GetMapping("/commission/income")
    public CommissionIncomeVo getCommissionIncome() {
        Long userId = SecurityUtils.getUserId();
        return commissionRecordService.getCommissionIncome(userId + "");
    }

    @ApiOperation("发起提现申请")
    @RequiresLogin(doIntercept = false)
    @PostMapping("/commission/withdraw")
    public void commissionWithdraw(@RequestBody @Validated CommissionWithdrawDto dto) {
        Long userId = SecurityUtils.getUserId();
        dto.setLoginId(userId + "");
        commissionRecordService.withdraw(dto);
    }


    @ApiOperation("获取所有会员选项")
    @GetMapping("/member/option")
    public List<MemberOpenOptionVo> getMemberOpenOptionList(MemberOpenOptionQueryDto queryDto) {
        return (List<MemberOpenOptionVo>) memberOpenOptionService.getAll(queryDto);
    }

    @ApiOperation("获取推广二维码")
    @GetMapping("/promotion/qrcode")
    public AjaxResult getPromotionQrCode(@RequestParam(required = false) String shopId,
                                         Integer width,
                                         Integer height,
                                         String content) throws IOException {
        String rootPath = "/opt/resources/";
        String fileName = MD5.create().digestHex(content) + ".png";
        File file = new File(rootPath + fileName);
        if (file.exists()) {
            return AjaxResult.success(fileName);
        }
        QrConfig config = new QrConfig(width, height);
        // 设置边距，既二维码和背景之间的边距
        config.setRatio(4);
        config.setMargin(3);
        //透明背景
        config.setErrorCorrection(ErrorCorrectionLevel.H);
        if (StringUtils.isNotBlank(shopId)) {
            VcShopShop shop = vcShopShopService.lambdaQuery().eq(VcShopShop::getId, shopId).one();
            Assert.notNull(shop, "商铺不存在");
            String logo = shop.getLogo();
            BufferedImage read = ImageIO.read(new URL(logo));
            config.setImg(read);
        }
        QrCodeUtil.generate(content, config, file);
        return AjaxResult.success(fileName);
    }


}
