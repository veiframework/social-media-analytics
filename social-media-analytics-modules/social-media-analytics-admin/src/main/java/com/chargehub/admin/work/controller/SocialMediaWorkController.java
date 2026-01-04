package com.chargehub.admin.work.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.admin.work.dto.SocialMediaWorkCreateDto;
import com.chargehub.admin.work.dto.SocialMediaWorkCreateQueryDto;
import com.chargehub.admin.work.dto.SocialMediaWorkPlayNumDto;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.service.SocialMediaWorkCreateService;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkCreateVo;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.log.annotation.Log;
import com.chargehub.common.log.enums.BusinessType;
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
    private SocialMediaWorkCreateService socialMediaWorkCreateService;

    @RequiresPermissions("work:page")
    @ApiOperation("分页")
    @Operation(summary = "分页")
    @GetMapping("/social-media/work")
    public IPage<SocialMediaWorkVo> getPage(SocialMediaWorkQueryDto queryDto) {
        if (CollectionUtils.isEmpty(queryDto.getUserId())) {
            Set<String> userIds = this.groupUserService.checkPurview();
            queryDto.setUserId(userIds);
        }
        queryDto.setTenantId(this.groupUserService.tenantPurview());
        return this.socialMediaWorkService.getPurviewPage(queryDto);
    }


    @RequiresLogin
    @ApiOperation("获取详情")
    @Operation(summary = "获取详情")
    @GetMapping("/social-media/work/{id}")
    public SocialMediaWorkVo getWorkDetail(@PathVariable String id) {
        return (SocialMediaWorkVo) this.socialMediaWorkService.getDetailById(id);
    }

    @Log(title = "作品管理", businessType = BusinessType.EXPORT)
    @RequiresLogin
    @ApiOperation("导出excel")
    @Operation(summary = "导出excel")
    @GetMapping("/social-media/work/export")
    public Z9CrudExportResult exportExcel(SocialMediaWorkQueryDto queryDto) {
        if (CollectionUtils.isEmpty(queryDto.getUserId())) {
            Set<String> userIds = this.groupUserService.checkPurview();
            queryDto.setUserId(userIds);
        }
        queryDto.setTenantId(this.groupUserService.tenantPurview());
        return this.socialMediaWorkService.exportExcel(queryDto);
    }


    @Debounce
    @RequiresLogin
    @ApiOperation("删除作品")
    @Operation(summary = "删除作品")
    @DeleteMapping("/social-media/work/{ids}")
    public void deleteWork(@PathVariable String ids) {
        this.socialMediaWorkService.deleteByIds(ids);
    }

    @Debounce
    @RequiresLogin
    @ApiOperation("更新播放量")
    @Operation(summary = "更新播放量")
    @PostMapping("/social-media/work/view")
    public void updateViewNum(@RequestBody @Validated SocialMediaWorkPlayNumDto dto) {
        this.socialMediaWorkService.updateViewNum(dto);
    }

    @RequiresLogin
    @ApiOperation("添加分享链接任务")
    @Operation(summary = "添加分享链接任务")
    @PostMapping("/social-media/work/share-link/task")
    public void createShareLinkTask(@RequestBody @Validated SocialMediaWorkCreateDto dto) {
        socialMediaWorkCreateService.create(dto);
    }

    @Debounce
    @RequiresLogin
    @ApiOperation("链接任务重试")
    @Operation(summary = "链接任务重试")
    @PostMapping("/social-media/work/share-link/retry/{id}")
    public void createShareLinkTaskRetry(@PathVariable String id) {
        socialMediaWorkCreateService.retryCreate(id);
    }

    @SuppressWarnings("unchecked")
    @RequiresLogin
    @ApiOperation("链接任务列表")
    @Operation(summary = "链接任务列表")
    @GetMapping("/social-media/work/share-link/task")
    public IPage<SocialMediaWorkCreateVo> createShareLinkTaskList(SocialMediaWorkCreateQueryDto queryDto) {
        queryDto.setCreator(SecurityUtils.getUserId() + "");
        return (IPage<SocialMediaWorkCreateVo>) this.socialMediaWorkCreateService.getPage(queryDto);
    }

    @Debounce
    @RequiresLogin
    @ApiOperation("删除链接任务")
    @Operation(summary = "删除链接任务")
    @DeleteMapping("/social-media/work/share-link/task/{ids}")
    public void deleteCreateShareLinkTask(@PathVariable String ids) {
        this.socialMediaWorkCreateService.deleteByIds(ids);
    }


    @RequiresLogin
    @ApiOperation("通过分享链接获取详情")
    @Operation(summary = "通过分享链接获取详情")
    @GetMapping("/social-media/work/share-link/detail")
    public SocialMediaWorkVo getWorkDetailByLink(String id) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Set<String> roles = loginUser.getRoles();
        String userId = loginUser.getUserid() + "";
        return this.socialMediaWorkService.getWorkDetail(id, roles, userId);
    }

}
