package com.chargehub.admin.statistic.controller;

import com.chargehub.admin.statistic.service.StatisticService;
import com.chargehub.admin.work.dto.SocialMediaWorkQueryDto;
import com.chargehub.admin.work.vo.SocialMediaWorkVo;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@UnifyResult
@RestController
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @RequiresPermissions("work:rank")
    @ApiOperation("获取作品排名")
    @Operation(summary = "获取作品排名")
    @GetMapping("/social-media/work/rank")
    public List<SocialMediaWorkVo> getWorkRankByGroup(SocialMediaWorkQueryDto queryDto) {
        return this.statisticService.getWorkRankByGroup(queryDto);
    }

}
