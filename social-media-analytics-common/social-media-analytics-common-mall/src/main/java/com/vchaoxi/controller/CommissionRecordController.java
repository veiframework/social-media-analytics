package com.vchaoxi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.vchaoxi.entity.WineBaseConfig;
import com.vchaoxi.param.CommissionRateConfigDto;
import com.vchaoxi.param.CommissionRecordQueryDto;
import com.vchaoxi.param.CommissionStatusDto;
import com.vchaoxi.service.IWineBaseConfigService;
import com.vchaoxi.service.impl.CommissionRecordService;
import com.vchaoxi.vo.CommissionRecordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 10:59
 */
@Api(tags = "提成记录管理")
@RestController
@RequestMapping("/commission/record")
@UnifyResult
public class CommissionRecordController {


    @Autowired
    private CommissionRecordService commissionRecordService;

    @Autowired
    private IWineBaseConfigService wineBaseConfigService;

    @ApiOperation("分页")
    @RequiresPermissions("commission:record:page")
    @GetMapping("/page")
    public IPage<CommissionRecordVo> getPage(CommissionRecordQueryDto queryDto) {
        return (IPage<CommissionRecordVo>) commissionRecordService.getPage(queryDto);
    }

    @ApiOperation("更新状态")
    @RequiresPermissions("commission:record:status")
    @PostMapping("/status")
    public void updateStatus(@RequestBody @Validated CommissionStatusDto dto) {
        commissionRecordService.updateStatus(dto);
    }

    /**
     * 获取提成配置
     *
     * @return
     */
    @ApiOperation("获取提成配置")
    @GetMapping("/commission/rate/config")
    public WineBaseConfig getCommissionConfig() {
        return wineBaseConfigService.getCommissionConfig();
    }

    /**
     * 更新提成比率
     *
     * @param dto
     */
    @ApiOperation("修改提成配置")
    @PostMapping("/commission/rate/config")
    public void updateCommissionRate(@RequestBody @Validated CommissionRateConfigDto dto) {
        wineBaseConfigService.updateCommissionRate(dto);
    }

}
