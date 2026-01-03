package com.chargehub.admin.account.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.dto.SocialMediaAccountDto;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.dto.SocialMediaAccountWechatVideoNicknameDto;
import com.chargehub.admin.account.dto.SocialMediaTransferAccountDto;
import com.chargehub.admin.account.mapper.SocialMediaAccountMapper;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.service.SocialMediaAccountTaskService;
import com.chargehub.admin.account.vo.SocialMediaAccountSelectorVo;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.controller.AbstractZ9Controller;
import com.chargehub.common.security.template.enums.Z9CrudApiCodeEnum;
import com.chargehub.common.security.utils.SecurityUtils;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@UnifyResult
@RequestMapping("/social-media/account")
@RestController
public class SocialMediaAccountController extends AbstractZ9Controller<SocialMediaAccountDto, SocialMediaAccountQueryDto, SocialMediaAccountVo, SocialMediaAccountService> {

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private SocialMediaAccountTaskService socialMediaAccountTaskService;

    @Autowired
    private SocialMediaAccountMapper socialMediaAccountMapper;

    protected SocialMediaAccountController(SocialMediaAccountService crudService) {
        super(crudService);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IPage<SocialMediaAccountVo> getPage(SocialMediaAccountQueryDto queryDto) {
        doCheckPermissions(Z9CrudApiCodeEnum.PAGE);
        if (CollectionUtils.isEmpty(queryDto.getUserId())) {
            Set<String> userIds = this.groupUserService.checkPurview();
            queryDto.setUserId(userIds);
        }
        queryDto.setTenantId(this.groupUserService.tenantPurview());
        return (IPage<SocialMediaAccountVo>) this.getCrudService().getPage(queryDto);
    }

    @RequiresLogin
    @GetMapping("/selector")
    public List<SocialMediaAccountSelectorVo> getAccountSelector(SocialMediaAccountQueryDto queryDto) {
        doCheckPermissions(Z9CrudApiCodeEnum.PAGE);
        if (CollectionUtils.isEmpty(queryDto.getUserId())) {
            Set<String> userIds = this.groupUserService.checkPurview();
            queryDto.setUserId(userIds);
        }
        queryDto.setTenantId(this.groupUserService.tenantPurview());
        return this.getCrudService().getAccountSelector(queryDto);
    }

    @RequiresLogin
    @Debounce
    @PostMapping("/wechat-video-nickname")
    @ApiOperation("微信视频号添加")
    public void createByWechatVideoNickname(@RequestBody @Validated SocialMediaAccountWechatVideoNicknameDto dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        dto.setUserId(loginUser.getUserid() + "");
        dto.setTenantId(loginUser.getShopId() + "");
        this.getCrudService().createByWechatVideoNickname(dto);
    }


    @RequiresLogin
    @Debounce
    @GetMapping("/sync/work/{accountId}")
    @ApiOperation("同步作品")
    public synchronized void syncWorkData(@PathVariable("accountId") String accountId) {
        SocialMediaAccount socialMediaAccount = this.socialMediaAccountMapper.doGetDetailById(accountId);
        if (socialMediaAccount == null) {
            return;
        }
        this.socialMediaAccountTaskService.batchAddTask(Sets.newHashSet(socialMediaAccount), false);
    }

    @RequiresPermissions("sync:all:work")
    @GetMapping("/sync/work")
    @ApiOperation("同步全部作品")
    public synchronized void syncWorkData() {
        Set<String> userIds = groupUserService.checkPurview();
        String tenantId = this.groupUserService.tenantPurview();
        List<SocialMediaAccount> accounts = this.getCrudService().getAccountIdsByUserIds(userIds, tenantId);
        this.socialMediaAccountTaskService.batchAddTask(accounts, false);
    }

    @Debounce
    @RequiresLogin
    @GetMapping("/account/{id}/sync/{autoSync}")
    @ApiOperation("是否启用自动同步")
    public void updateAutoSync(@PathVariable String id, @PathVariable String autoSync) {
        this.getCrudService().updateAutoSync(id, autoSync);
    }

    @Debounce
    @RequiresLogin
    @PostMapping("/account/transfer")
    @ApiOperation("是否启用自动同步")
    public void transferAccount(@RequestBody @Validated SocialMediaTransferAccountDto transferAccountDto) {
        this.getCrudService().transferAccount(transferAccountDto);
    }

}
