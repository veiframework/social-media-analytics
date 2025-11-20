package com.chargehub.admin.work.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.domain.Z9CrudExportResult;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@UnifyResult
@RestController
public class SocialMediaWorkController {

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;


    @RequiresPermissions("work:page")
    @ApiOperation("分页")
    @Operation(summary = "分页")
    @GetMapping("/social-media/work")
    public IPage<SocialMediaWorkVo> getPage(SocialMediaWorkQueryDto queryDto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return this.socialMediaWorkService.getPurviewPage(queryDto, loginUser);
    }


    @ApiOperation("获取详情")
    @Operation(summary = "获取详情")
    @GetMapping("/social-media/work/{id}")
    public SocialMediaWorkVo getWorkDetail(@PathVariable String id) {
        return (SocialMediaWorkVo) this.socialMediaWorkService.getDetailById(id);
    }

    @ApiOperation("导出excel")
    @Operation(summary = "导出excel")
    @GetMapping("/social-media/work/export")
    public Z9CrudExportResult exportExcel(SocialMediaWorkQueryDto queryDto) {
        return this.socialMediaWorkService.exportExcel(queryDto);
    }

}
