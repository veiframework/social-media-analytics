package com.chargehub.admin.work.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.account.service.SocialMediaAccountService;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.dto.SocialMediaWorkShareLinkDto;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.domain.Z9CrudExportResult;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
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
@RestController
public class SocialMediaWorkController {

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @RequiresPermissions("work:page")
    @ApiOperation("分页")
    @Operation(summary = "分页")
    @GetMapping("/social-media/work")
    public IPage<SocialMediaWorkVo> getPage(SocialMediaWorkQueryDto queryDto) {
        if (CollectionUtils.isEmpty(queryDto.getUserId())) {
            Set<String> userIds = this.groupUserService.checkPurview();
            queryDto.setUserId(userIds);
        }
        return this.socialMediaWorkService.getPurviewPage(queryDto);
    }


    @RequiresLogin
    @ApiOperation("获取详情")
    @Operation(summary = "获取详情")
    @GetMapping("/social-media/work/{id}")
    public SocialMediaWorkVo getWorkDetail(@PathVariable String id) {
        return (SocialMediaWorkVo) this.socialMediaWorkService.getDetailById(id);
    }

    @RequiresLogin
    @ApiOperation("导出excel")
    @Operation(summary = "导出excel")
    @GetMapping("/social-media/work/export")
    public Z9CrudExportResult exportExcel(SocialMediaWorkQueryDto queryDto) {
        if (CollectionUtils.isEmpty(queryDto.getUserId())) {
            Set<String> userIds = this.groupUserService.checkPurview();
            queryDto.setUserId(userIds);
        }
        return this.socialMediaWorkService.exportExcel(queryDto);
    }

    @Debounce
    @RequiresLogin
    @ApiOperation("通过分享链接添加作品")
    @Operation(summary = "通过分享链接添加作品")
    @PostMapping("/social-media/work/share-link")
    public void createByShareUrl(@RequestBody @Validated SocialMediaWorkShareLinkDto dto) {
        Long userId = SecurityUtils.getUserId();
        dto.setUserId(userId + "");
        this.socialMediaAccountService.createWorkByShareUrl(dto);
    }

    @Debounce
    @RequiresLogin
    @ApiOperation("微信视频号ID添加")
    @Operation(summary = "微信视频号ID添加")
    @PostMapping("/social-media/work/wechat-video-id")
    public void createByWechatVideoId(@RequestBody @Validated SocialMediaWorkShareLinkDto dto) {
        Long userId = SecurityUtils.getUserId();
        dto.setUserId(userId + "");
        dto.setPlatformEnum(SocialMediaPlatformEnum.WECHAT_VIDEO);
        this.socialMediaAccountService.createWorkByShareUrl(dto);
    }


    @Debounce
    @RequiresLogin
    @ApiOperation("删除作品")
    @Operation(summary = "删除作品")
    @DeleteMapping("/social-media/work/{ids}")
    public void deleteWork(@PathVariable String ids){
        this.socialMediaWorkService.deleteByIds(ids);
    }

}
