package com.chargehub.biz.admin.controller;

import com.chargehub.admin.api.domain.SysDept;
import com.chargehub.admin.api.domain.SysRole;
import com.chargehub.admin.api.domain.SysUser;
import com.chargehub.admin.api.model.LoginUser;

import com.chargehub.biz.admin.domain.SysUserStationDto;
import com.chargehub.biz.admin.mapper.SysRoleMapper;
import com.chargehub.biz.admin.mapper.SysUserMapper;
import com.chargehub.biz.admin.service.*;
import com.chargehub.common.core.domain.R;
import com.chargehub.common.core.utils.StringUtils;
import com.chargehub.common.core.utils.poi.ExcelUtil;
import com.chargehub.common.core.web.controller.BaseController;
import com.chargehub.common.core.web.domain.AjaxResult;
import com.chargehub.common.core.web.domain.AjaxResultT;
import com.chargehub.common.core.web.page.TableDataInfo;
import com.chargehub.common.log.annotation.Log;
import com.chargehub.common.log.enums.BusinessType;
import com.chargehub.common.security.annotation.InnerAuth;
import com.chargehub.common.security.annotation.Logical;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysPermissionService permissionService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;



    /**
     * 获取用户列表
     */
    @RequiresPermissions(value = {"system:user:list","partner:user:list","inspector:user:list"}, logical = Logical.OR)
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        for(SysUser sysUser : list){
            sysUser.setRoles(sysRoleMapper.selectRolePermissionByUserId(sysUser.getUserId()));
        }
        return getDataTable(list);
    }


    /**
     * 获取全部的用户列表
     * @return
     */
    @RequiresPermissions()
    @GetMapping("/all")
    public AjaxResultT<List<SysUser>> allUserList()
    {
        List<SysUser> list = userService.selectUserList(new SysUser());
        return AjaxResultT.success(list);
    }



    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:user:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @RequiresPermissions("system:user:import")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 获取当前用户信息
     */
    @InnerAuth
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable("username") String username)
    {
        SysUser sysUser = userService.selectUserByUserName(username);
        if (StringUtils.isNull(sysUser))
        {
            return R.fail("用户名或密码错误");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        sysUserVo.setShopId(sysUser.getShopId());
        return R.ok(sysUserVo);
    }


    /**
     * 更新用户最后登录信息
     * @param userId
     * @param ipAddress
     * @return
     */
    @InnerAuth
    @PostMapping("/last-login-info/{userId}")
    public R<Boolean> updateLastLoginInfo(@PathVariable("userId") Long userId,@RequestParam("ipAddress") String ipAddress)
    {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setLoginDate(new Date());
        user.setLoginIp(ipAddress);
        Boolean boo = userMapper.updateUser(user) > 0;
        return R.ok(boo);
    }



    /**
     * 根据用户ID获取用户信息
     */
    @InnerAuth
    @GetMapping("/info_by_id/{userid}")
    public R<LoginUser> getUserInfoById(@PathVariable("userid") String userid)
    {
        SysUser sysUser = userService.selectUserById(Long.valueOf(userid));
        if (StringUtils.isNull(sysUser))
        {
            return R.fail("用户ID不存在");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    /**
     * 注册用户信息
     */
    @InnerAuth
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody SysUser sysUser)
    {
        String username = sysUser.getUserName();
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return R.fail("当前系统没有开启注册功能！");
        }
        if (!userService.checkUserNameUnique(sysUser))
        {
            return R.fail("保存用户'" + username + "'失败，注册账号已存在");
        }
        return R.ok(userService.registerUser(sysUser));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = userService.selectUserById(SecurityUtils.getUserId());
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
//        ajax.put("isJtAdmin", SecurityUtils.getLoginUser().isJtAdmin() ? 1 : 0);
        return ajax;
    }

    /**
     * 根据用户编号获取详细信息
     */
    @RequiresPermissions(value = {"system:user:query","partner:user:query","inspector:user:query"},logical = Logical.OR)
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        userService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @RequiresPermissions(value = {"system:user:add","partner:user:add","inspector:user:add"},logical = Logical.OR)
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
        if(StringUtils.isEmpty(user.getNickName())){
            user.setNickName(user.getUserName());
        }
        if (!userService.checkUserNameUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getNickname());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        userService.insertUser(user);
        return AjaxResult.success(user);
    }

    /**
     * 修改用户
     */
    @RequiresPermissions(value = {"system:user:edit","partner:user:edit","inspector:user:edit"},logical = Logical.OR)
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (!userService.checkUserNameUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        userService.updateUser(user);
        return AjaxResult.success(user);
    }

    /**
     * 删除用户
     */
    @RequiresPermissions(value = {"system:user:remove","partner:user:remove","inspector:user:remove"},logical = Logical.OR)
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, SecurityUtils.getUserId()))
        {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @RequiresPermissions(value = {"system:user:edit","partner:user:edit","inspector:user:edit"},logical = Logical.OR)
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @RequiresPermissions(value = {"system:user:edit","partner:user:edit","inspector:user:edit"},logical = Logical.OR)
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @RequiresPermissions(value = {"system:user:query","partner:user:query","inspector:user:query"},logical = Logical.OR)
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @RequiresPermissions(value = {"system:user:edit","partner:user:edit","inspector:user:edit"},logical = Logical.OR)
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 获取部门树列表
     */
    @RequiresPermissions(value = {"system:user:list","partner:user:list","inspector:user:list"}, logical = Logical.OR)
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        return success(deptService.selectDeptTreeList(dept));
    }

    @ApiOperation("修改巡检员场站权限")
    @PostMapping("/inspector/station")
    public AjaxResult updateStationIds(@RequestBody @Validated SysUserStationDto dto){
        SysUser sysUser = new SysUser();
        sysUser.setUserId(Long.parseLong(dto.getUserId()));
        Set<String> inspectorStationIds = dto.getInspectorStationIds();
        Set<String> partnerStationIds = dto.getPartnerStationIds();
        if(inspectorStationIds != null) {
            sysUser.setInspectorStationIds(String.join(",", inspectorStationIds));
        }
        if(partnerStationIds != null) {
            sysUser.setPartnerStationIds(String.join(",", partnerStationIds));
        }
        if(inspectorStationIds == null && partnerStationIds == null){
            throw new IllegalArgumentException("请设置数据权限");
        }
        this.userMapper.updateUser(sysUser);
        return success();
    }

    @ApiOperation("根据场站id获取巡检员")
    @GetMapping("/inspector/station/{stationId}")
    public AjaxResult getInspectorByStationId(@PathVariable String stationId) {
        return success(this.userMapper.getInspectorByStationId(stationId));
    }


    @ApiOperation("根据区域id获取巡检员")
    @GetMapping("/inspector/region/{regionId}")
    public AjaxResult getInspectorByRegionId(@PathVariable String regionId){
        return success(this.userMapper.getInspectorByRegionId(regionId));
    }


}
