package com.vchaoxi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.vchaoxi.param.CommissionStatusDto;
import com.vchaoxi.param.MemberOpenOptionDto;
import com.vchaoxi.param.MemberOpenOptionQueryDto;
import com.vchaoxi.service.impl.MemberOpenOptionService;
import com.vchaoxi.vo.MemberOpenOptionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 16:08
 */
@Api(tags = "会员开通选项管理")
@RestController
@RequestMapping("/member/option")
@UnifyResult
public class MemberOpenOptionController {

    @Autowired
    private MemberOpenOptionService memberOpenOptionService;


    @ApiOperation("分页")
    @RequiresPermissions("member:option:page")
    @GetMapping("/page")
    public IPage<MemberOpenOptionVo> getPage(MemberOpenOptionQueryDto queryDto) {
        return (IPage<MemberOpenOptionVo>) memberOpenOptionService.getPage(queryDto);
    }

    @ApiOperation("修改")
    @RequiresPermissions("member:option:edit")
    @PutMapping
    public void edit(@RequestBody @Validated MemberOpenOptionDto dto) {
        memberOpenOptionService.edit(dto, dto.getId() + "");
    }



}
