package com.chargehub.admin.groupuser.controller;

import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.groupuser.dto.GroupUserBatchAddDto;
import com.chargehub.admin.groupuser.dto.GroupUserQueryDto;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.admin.groupuser.vo.GroupUserVo;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@UnifyResult
@RestController
@RequestMapping("/social-media/group-user")
public class GroupUserController {

    private final GroupUserService groupUserService;

    public GroupUserController(GroupUserService groupUserService) {
        this.groupUserService = groupUserService;
    }

    @Debounce
    @RequiresPermissions("group:user:batchadd")
    @ApiOperation("批量关联员工")
    @Operation(summary = "批量关联员工")
    @PostMapping("/batch")
    public void batchAdd(@RequestBody @Validated GroupUserBatchAddDto dto) {
        String userId = SecurityUtils.getUserId() + "";
        dto.setParentUserId(userId);
        groupUserService.batchAdd(dto);
    }

    @RequiresPermissions("group:user:all")
    @ApiOperation("获取全部")
    @Operation(summary = "获取全部")
    @GetMapping("/all")
    public List<GroupUserVo> getAll(GroupUserQueryDto queryDto) {
        String userId = SecurityUtils.getUserId() + "";
        queryDto.setParentUserId(userId);
        return (List<GroupUserVo>) this.groupUserService.getAll(queryDto);
    }


    @RequiresPermissions("group:user:delete")
    @Debounce
    @ApiOperation("删除")
    @Operation(summary = "删除")
    @DeleteMapping("/{ids}")
    public void deleteByIds(@PathVariable String ids) {
        this.groupUserService.deleteByIds(ids);
    }

    @ApiOperation("获取用户下拉列表")
    @Operation(summary = "获取用户下拉列表")
    @GetMapping("/user/selector")
    public List<SysUser> getUserSelector() {
        return this.groupUserService.getUsers();
    }

}
