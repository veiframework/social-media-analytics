package com.chargehub.admin.account.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.account.dto.SocialMediaAccountDto;
import com.chargehub.admin.account.dto.SocialMediaAccountQueryDto;
import com.chargehub.admin.account.dto.SocialMediaAccountShareLinkDto;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.account.vo.SocialMediaAccountVo;
import com.chargehub.admin.datasync.DataSyncWorkScheduler;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.controller.AbstractZ9Controller;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
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
    private GroupUserService groupUserService;

    protected SocialMediaAccountController(SocialMediaAccountService crudService) {
        super(crudService);
    }

    @Override
    public IPage<SocialMediaAccountVo> getPage(SocialMediaAccountQueryDto queryDto) {
        Set<String> userIds = groupUserService.checkPurview();
        queryDto.setUserId(userIds);
        return super.getPage(queryDto);
    }

    @Debounce
    @PostMapping("/share-link")
    @ApiOperation("根据分享链接添加账号")
    public void createByShareLink(@RequestBody @Validated SocialMediaAccountShareLinkDto dto) {
        Long userId = SecurityUtils.getUserId();
        dto.setUserId(userId + "");
        this.getCrudService().createByShareLink(dto);
    }


    @Debounce
    @GetMapping("/sync/work/{accountId}")
    @ApiOperation("同步作品")
    public void syncWorkData(@PathVariable("accountId") String accountId) {
        this.dataSyncWorkScheduler.asyncExecute(accountId);
    }

}
