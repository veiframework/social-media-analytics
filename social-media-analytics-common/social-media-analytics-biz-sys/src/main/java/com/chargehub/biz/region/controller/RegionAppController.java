package com.chargehub.biz.region.controller;


import com.chargehub.biz.region.domain.dto.RegionAppQueryDto;
import com.chargehub.biz.region.domain.vo.RegionAppVo;
import com.chargehub.biz.region.service.RegionService;
import com.chargehub.common.security.annotation.UnifyResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhanghaowei
 * @date 2024/05/15 09:33
 */
@UnifyResult
@RestController
@RequestMapping("/regions/app")
@Tag(name = "区域APP管理", description = "区域APP管理")
public class RegionAppController {

    @Autowired
    private RegionService regionService;

    @Operation(summary = "获取全部")
    @GetMapping("/all")
    public RegionAppVo getAppRegionList(@Validated RegionAppQueryDto queryDto) {
        return this.regionService.getAppRegionList(queryDto);
    }


}
