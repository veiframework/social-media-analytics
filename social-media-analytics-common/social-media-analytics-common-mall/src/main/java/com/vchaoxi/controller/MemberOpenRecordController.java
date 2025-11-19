package com.vchaoxi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.vchaoxi.param.MemberOpenOptionDto;
import com.vchaoxi.param.MemberOpenOptionQueryDto;
import com.vchaoxi.param.MemberOpenRecordQueryDto;
import com.vchaoxi.service.impl.MemberOpenRecordService;
import com.vchaoxi.vo.MemberOpenOptionVo;
import com.vchaoxi.vo.MemberOpenRecordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 17:32
 */
@Api(tags = "会员开通记录管理")
@RestController
@RequestMapping("/member/record")
@UnifyResult
public class MemberOpenRecordController {


    @Autowired
    private MemberOpenRecordService memberOpenRecordService;


    @ApiOperation("分页")
    @RequiresPermissions("member:record:page")
    @GetMapping("/page")
    public IPage<MemberOpenRecordVo> getPage(MemberOpenRecordQueryDto queryDto) {
        return (IPage<MemberOpenRecordVo>) memberOpenRecordService.getPage(queryDto);
    }





}
