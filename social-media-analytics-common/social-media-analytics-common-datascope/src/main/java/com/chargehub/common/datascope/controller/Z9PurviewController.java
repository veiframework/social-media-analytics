package com.chargehub.common.datascope.controller;

import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.datascope.domain.Z9Selector;
import com.chargehub.common.datascope.domain.vo.Z9PurviewCreateOrUpdateVo;
import com.chargehub.common.datascope.domain.vo.Z9PurviewTreeQueryVo;
import com.chargehub.common.datascope.domain.vo.Z9PurviewTreeVo;
import com.chargehub.common.datascope.service.Z9PurviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 13:11
 */
@RestController
@RequestMapping("/purview")
@Tag(name = "数据权限管理", description = "数据权限管理")
public class Z9PurviewController {


    @Autowired
    private Z9PurviewService z9PurviewService;


    @Operation(summary = "获取数据权限选项")
    @GetMapping("/selector")
    public List<Z9Selector> getSelector(Z9PurviewTreeQueryVo queryVo) {
        return z9PurviewService.getSelector(queryVo.getPurviewTypes());
    }

    @Operation(summary = "获取权限树")
    @GetMapping("/tree/owner/{ownerId}/type/{purviewType}")
    public Z9PurviewTreeVo getPurviewTrees(@PathVariable String ownerId,
                                           @PathVariable String purviewType) {
        return z9PurviewService.getPurviewTrees(ownerId, purviewType);
    }

    @Operation(summary = "添加权限")
    @PostMapping
    public AjaxResult addPurview(@RequestBody @Validated Z9PurviewCreateOrUpdateVo createOrUpdateVo) {
        z9PurviewService.addPurview(createOrUpdateVo);
        return AjaxResult.success();
    }

    @Operation(summary = "获取选中的数据权限")
    @GetMapping("/checked/type")
    public AjaxResult getCheckedPurviewType(String ownerId) {
        return AjaxResult.success(z9PurviewService.getCheckedPurviewType(ownerId));
    }


}
