package com.chargehub.admin.groupuser.controller;

import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.groupuser.dto.GroupUserBatchAddDto;
import com.chargehub.admin.groupuser.dto.GroupUserQueryDto;
import com.chargehub.admin.groupuser.service.GroupUserService;
import com.chargehub.admin.groupuser.vo.GroupUserVo;
import com.chargehub.biz.admin.service.ISysUserService;
import com.chargehub.common.security.annotation.Debounce;
import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.UnifyResult;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@UnifyResult
@RestController
@RequestMapping("/social-media/group-user")
public class GroupUserController {

    private final GroupUserService groupUserService;

    @Autowired
    private ISysUserService sysUserService;

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

    @RequiresLogin
    @ApiOperation("获取用户下拉列表")
    @Operation(summary = "获取用户下拉列表")
    @GetMapping("/user/selector")
    public List<SysUser> getUserSelector() {
        return this.groupUserService.getUsers();
    }

    @RequiresLogin
    @ApiOperation("获取group用户下拉列表")
    @Operation(summary = "获取group用户下拉列表")
    @GetMapping("/user/query/selector")
    public List<GroupUserVo> getGroupUsers() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser.isAdmin() || loginUser.isSuperAdmin()) {
            List<SysUser> list = sysUserService.selectUserList(new SysUser());
            return list.stream().map(i -> {
                GroupUserVo vo = new GroupUserVo();
                vo.setId(i.getUserId() + "");
                vo.setUserId(i.getUserId() + "");
                return vo;
            }).collect(Collectors.toList());
        }
        String userId = SecurityUtils.getUserId() + "";
        return this.groupUserService.getGroupUsers(userId);
    }

    @RequiresLogin
    @ApiOperation("获取用户下拉列表")
    @Operation(summary = "获取用户下拉列表")
    @GetMapping("/user/leader")
    public List<SysUser> getLeaderUsers() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Set<String> roles = loginUser.getRoles();
        SysUser sysUser = new SysUser();
        sysUser.setNickName(loginUser.getSysUser().getNickName());
        sysUser.setUserId(loginUser.getUserid());
        if (roles.contains("组长") || roles.contains("员工")) {
            return Stream.of(sysUser).collect(Collectors.toList());
        }
        return this.groupUserService.getLeaderUsers();
    }

}
