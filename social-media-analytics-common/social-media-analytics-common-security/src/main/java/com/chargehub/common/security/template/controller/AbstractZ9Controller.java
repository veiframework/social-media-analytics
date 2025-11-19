package com.chargehub.common.security.template.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.template.domain.*;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.enums.Z9CrudApiCodeEnum;
import com.chargehub.common.security.template.event.AfterCreateExportEvent;
import com.chargehub.common.security.template.service.Z9CrudService;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @since 2023-12-13 16:14
 */
@SuppressWarnings("all")
public abstract class AbstractZ9Controller<T extends Z9CrudDto, Q extends Z9CrudQueryDto, V extends Z9CrudVo, S extends Z9CrudService> implements ApplicationContextAware {

    private Boolean debug;

    private final S crudService;

    private Map<Z9CrudApiCodeEnum, Z9CrudPermission> defaultPermissions;

    protected AbstractZ9Controller(S crudService) {
        this.crudService = crudService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        Environment environment = applicationContext.getEnvironment();
        this.debug = environment.getProperty("z9.crud.debug", Boolean.class, false);
        this.defaultPermissions = Z9CrudApiCodeEnum.autoGeneratePermissionId(this.crudService.dtoClass(), this.debug);
    }

    public S getCrudService() {
        return crudService;
    }

    @Debounce
    @ApiOperation("创建")
    @Operation(summary = "创建")
    @PostMapping
    public void create(@RequestBody @Validated T dto) {
        doCheckPermissions(Z9CrudApiCodeEnum.CREATE);
        this.crudService.create(dto);
    }

    @Debounce
    @ApiOperation("修改")
    @Operation(summary = "修改")
    @PutMapping("/{id}")
    public void edit(@PathVariable String id,
                     @RequestBody @Validated T dto) {
        doCheckPermissions(Z9CrudApiCodeEnum.EDIT);
        this.crudService.edit(dto, id);
    }

    @Debounce
    @ApiOperation("删除")
    @Operation(summary = "删除")
    @DeleteMapping("/{ids}")
    public void deleteByIds(@PathVariable String ids) {
        doCheckPermissions(Z9CrudApiCodeEnum.DELETE);
        this.crudService.deleteByIds(ids);
    }

    @ApiOperation("详情")
    @Operation(summary = "详情")
    @GetMapping("/{id}")
    public V getDetailById(@PathVariable String id) {
        doCheckPermissions(Z9CrudApiCodeEnum.DETAILS);
        return (V) this.crudService.getDetailById(id);
    }

    @ApiOperation("分页")
    @Operation(summary = "分页")
    @GetMapping
    public IPage<V> getPage(Q queryDto) {
        doCheckPermissions(Z9CrudApiCodeEnum.PAGE);
        return this.crudService.getPage(queryDto);
    }

    @ApiOperation("获取全部")
    @Operation(summary = "获取全部")
    @GetMapping("/all")
    public List<V> getAll(Q queryDto) {
        doCheckPermissions(Z9CrudApiCodeEnum.LIST);
        return this.crudService.getAll(queryDto);
    }

    @Debounce
    @ApiOperation("异步导出")
    @Operation(summary = "异步导出")
    @PostMapping("/export/async")
    public void asyncExport(AfterCreateExportEvent event) {
        this.crudService.publishEvent(event);
    }


    @ApiOperation("导出excel")
    @Operation(summary = "导出excel")
    @GetMapping("/export")
    public Z9CrudExportResult exportExcel(Q queryDto) {
        doCheckPermissions(Z9CrudApiCodeEnum.EXPORT);
        return this.crudService.exportExcel(queryDto);
    }

    @ApiOperation("导入数据")
    @Operation(summary = "导入数据")
    @PostMapping("/import")
    public Z9CrudImportResult<T> importExcel(@RequestBody @Validated ExcelImportDto importDto) {
        doCheckPermissions(Z9CrudApiCodeEnum.IMPORT);
        return this.crudService.importExcel(importDto);
    }

    @ApiOperation("下载excel模板")
    @Operation(summary = "下载excel模板")
    @GetMapping("/import-template")
    public void exportExcelTemplate(HttpServletResponse response) {
        doCheckPermissions(Z9CrudApiCodeEnum.EXCEL_TEMPLATE);
        this.crudService.exportExcelTemplate(response);
    }


    public void doCheckPermissions(Z9CrudApiCodeEnum code) {
        if (debug) {
            return;
        }
        if (MapUtil.isNotEmpty(requireRoleMap())) {
            requireRoleMap().getOrDefault(code, Z9CrudRole.nullRole()).doCheck();
        }
        if (MapUtil.isNotEmpty(requirePermissionMap())) {
            requirePermissionMap().getOrDefault(code, Z9CrudPermission.nullPermission()).doCheck();
        }
    }

    public Map<Z9CrudApiCodeEnum, Z9CrudPermission> requirePermissionMap() {
        return defaultPermissions;
    }

    public Map<Z9CrudApiCodeEnum, Z9CrudRole> requireRoleMap() {
        return new HashMap<>();
    }
}
