package com.chargehub.biz.region.controller;


import com.chargehub.biz.region.domain.dto.RegionDto;
import com.chargehub.biz.region.domain.dto.RegionQueryDto;
import com.chargehub.biz.region.domain.dto.RegionVisibleDto;
import com.chargehub.biz.region.domain.vo.RegionVo;
import com.chargehub.biz.region.service.RegionService;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.controller.AbstractZ9Controller;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 相关接口
 *
 * @author Zhanghaowei
 * @date 2021-11-05 14:42:56
 */
@UnifyResult
@RestController
@RequestMapping("/regions")
@Tag(name = "区域管理", description = "区域管理")
public class RegionController extends AbstractZ9Controller<RegionDto, RegionQueryDto, RegionVo, RegionService> {


    protected RegionController(RegionService crudService) {
        super(crudService);
    }

//    @RequiresPermissions("region:visible:edit")
    @ApiOperation("编辑区域是否可见")
    @Operation(summary = "编辑区域是否可见")
    @PostMapping("/visible")
    public Boolean updateChargeConfig(@RequestBody @Validated RegionVisibleDto visibleDto) {
        this.getCrudService().updateVisible(visibleDto);
        return true;
    }
}

