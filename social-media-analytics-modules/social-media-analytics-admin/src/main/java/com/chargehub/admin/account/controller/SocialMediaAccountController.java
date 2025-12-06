package com.chargehub.admin.account.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.account.dto.*;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.admin.scheduler.DataSyncWorkScheduler;
import com.chargehub.admin.scheduler.DataSyncWorkSchedulerV2;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.RequiresLogin;
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
    private DataSyncWorkScheduler dataSyncWorkScheduler;

    @Autowired
    private DataSyncWorkSchedulerV2 dataSyncWorkSchedulerV2;

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private RedisService redisService;

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
        return (IPage<SocialMediaAccountVo>) this.getCrudService().getPage(queryDto);
    }

    @RequiresLogin
    @Debounce
    @PostMapping("/share-link")
    @ApiOperation("根据分享链接添加账号")
    public void createByShareLink(@RequestBody @Validated SocialMediaAccountShareLinkDto dto) {
        Long userId = SecurityUtils.getUserId();
        dto.setUserId(userId + "");
        this.getCrudService().createByShareLink(dto);
    }

    @RequiresLogin
    @Debounce
    @PostMapping("/wechat-video-nickname")
    @ApiOperation("微信视频号添加")
    public void createByWechatVideoNickname(@RequestBody @Validated SocialMediaAccountWechatVideoNicknameDto dto) {
        Long userId = SecurityUtils.getUserId();
        dto.setUserId(userId + "");
        this.getCrudService().createByWechatVideoNickname(dto);
    }


    @RequiresLogin
    @Debounce
    @GetMapping("/sync/work/{accountId}")
    @ApiOperation("同步作品")
    public void syncWorkData(@PathVariable("accountId") String accountId) {
        SocialMediaAccountVo detailById = (SocialMediaAccountVo) this.getCrudService().getDetailById(accountId);
        if (detailById == null) {
            return;
        }
        if (detailById.getPlatformId().equals(SocialMediaPlatformEnum.WECHAT_VIDEO.getDomain())) {
            this.dataSyncWorkScheduler.asyncExecute(Sets.newHashSet(accountId));
        } else {
            this.dataSyncWorkSchedulerV2.asyncExecute(Sets.newHashSet(accountId));
        }
    }

    @RequiresLogin
    @GetMapping("/sync/work")
    @ApiOperation("同步全部作品")
    public void syncWorkData() {
        Set<String> userIds = groupUserService.checkPurview();
        Set<String> accountIds = this.getCrudService().getAccountIdsByUserIds(userIds);
        this.dataSyncWorkSchedulerV2.asyncExecute(accountIds);
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
