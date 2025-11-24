package com.chargehub.admin.account.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.account.dto.SocialMediaAccountDto;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.dto.SocialMediaAccountShareLinkDto;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.scheduler.DataSyncWorkScheduler;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.common.redis.service.RedisService;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.controller.AbstractZ9Controller;
import com.chargehub.common.security.template.enums.Z9CrudApiCodeEnum;
import com.chargehub.common.security.utils.SecurityUtils;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        Set<String> userIds = groupUserService.checkPurview();
        queryDto.setUserId(userIds);
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
    @GetMapping("/sync/work/{accountId}")
    @ApiOperation("同步作品")
    public void syncWorkData(@PathVariable("accountId") String accountId) {
        this.dataSyncWorkScheduler.asyncExecute(Sets.newHashSet(accountId));
    }

    @RequiresLogin
    @GetMapping("/sync/work")
    @ApiOperation("同步全部作品")
    public void syncWorkData() {
        Long userId = SecurityUtils.getUserId();
        String lockName = "lock:sync-work:user:" + userId;
        Boolean set = redisService.setNx(lockName, 1, 1L, TimeUnit.MINUTES);
        Assert.isTrue(set, "同步数据需要时间，请稍等");
        Set<String> userIds = groupUserService.checkPurview();
        Set<String> accountIds = this.getCrudService().getAccountIdsByUserIds(userIds);
        this.dataSyncWorkScheduler.asyncExecute(accountIds);
    }

}
