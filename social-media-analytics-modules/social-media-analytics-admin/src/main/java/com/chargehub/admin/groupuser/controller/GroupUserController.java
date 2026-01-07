package com.chargehub.admin.groupuser.controller;

import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.admin.groupuser.domain.GroupUser;
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
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
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
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser.isLeader()) {
            GroupUser groupUser = groupUserService.getByUserId(userId);
            Assert.notNull(groupUser, "请先咨询主管关联当前账号");
        }
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

    //组员管理在用
    @RequiresLogin
    @ApiOperation("获取用户下拉列表")
    @Operation(summary = "获取用户下拉列表")
    @GetMapping("/user/selector")
    public List<SysUser> getUserSelector() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String tenantId = null;
        if (loginUser.isNormalUser()) {
            tenantId = loginUser.getShopId() + "";
        }
        return this.groupUserService.getUsers(tenantId);
    }

    //作品列表在用，首页在用，账号列表在用
    @RequiresLogin
    @ApiOperation("获取group用户下拉列表")
    @Operation(summary = "获取group用户下拉列表")
    @GetMapping("/user/query/selector")
    public List<GroupUserVo> getGroupUsers() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (!loginUser.isNormalUser()) {
            List<SysUser> list = sysUserService.selectUserList(new SysUser());
            return list.stream().map(i -> {
                GroupUserVo vo = new GroupUserVo();
                vo.setId(i.getUserId() + "");
                vo.setUserId(i.getUserId() + "");
                return vo;
            }).collect(Collectors.toList());
        }
        if (loginUser.isSuperAdmin()) {
            SysUser sysUser = new SysUser();
            sysUser.setShopId(loginUser.getShopId());
            List<SysUser> list = sysUserService.selectUserList(sysUser);
            return list.stream().map(i -> {
                GroupUserVo vo = new GroupUserVo();
                vo.setId(i.getUserId() + "");
                vo.setUserId(i.getUserId() + "");
                return vo;
            }).collect(Collectors.toList());
        }
        if (loginUser.isLabor()) {
            GroupUserVo vo = new GroupUserVo();
            vo.setId(loginUser.getUserid() + "");
            vo.setUserId(loginUser.getUserid() + "");
            return Lists.newArrayList(vo);
        }

        String userId = SecurityUtils.getUserId() + "";
        return this.groupUserService.getGroupUsers(userId);
    }

    //数据分析在用
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
        String tenantId = null;
        if (loginUser.isNormalUser()) {
            tenantId = loginUser.getShopId() + "";
        }
        return this.groupUserService.getLeaderUsers(tenantId);
    }

}
