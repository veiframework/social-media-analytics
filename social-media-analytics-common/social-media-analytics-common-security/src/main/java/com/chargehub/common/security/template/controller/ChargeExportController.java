package com.chargehub.common.security.template.controller;

import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.template.dto.ChargeExportDto;
import com.chargehub.common.security.template.dto.ChargeExportQueryDto;
import com.chargehub.common.security.template.enums.Z9CrudApiCodeEnum;
import com.chargehub.common.security.template.service.ChargeExportService;
import com.chargehub.common.security.template.vo.ChargeExportVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhanghaowei
 * @date 2024/07/16 17:23
 */
@UnifyResult
@RestController
@RequestMapping("/common/export")
@Api(tags = "公共导出功能", description = "")
public class ChargeExportController extends AbstractZ9Controller<ChargeExportDto, ChargeExportQueryDto, ChargeExportVo, ChargeExportService> {


    protected ChargeExportController(ChargeExportService crudService) {
        super(crudService);
    }

    @Debounce
    @ApiOperation(value = "重新导出")
    @PostMapping(value = "/retry")
    public AjaxResult retryExport(@RequestParam String id) {
        this.getCrudService().export(id);
        return AjaxResult.success();
    }

    @Debounce
    @ApiOperation(value = "终止导出")
    @PostMapping(value = "/termination")
    public AjaxResult termination(String id){
        this.getCrudService().termination(id);
        return AjaxResult.success();
    }

    @Override
    public void doCheckPermissions(Z9CrudApiCodeEnum code) {
        //nothing to do
    }
}
