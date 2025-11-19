package com.chargehub.auth.controller;

import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.auth.form.LoginBody;
import com.chargehub.auth.form.RegisterBody;
import com.chargehub.auth.service.SysLoginService;
import com.chargehub.common.core.domain.R;
import com.chargehub.common.core.exception.ServiceException;
import com.chargehub.common.core.utils.JwtUtils;
import com.chargehub.common.core.utils.StringUtils;
import com.chargehub.common.security.auth.AuthUtil;
import com.chargehub.common.security.service.InspectorTokenService;
import com.chargehub.common.security.service.TokenService;
import com.chargehub.common.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * token 控制
 *
 * @author ruoyi
 */
@RestController
@Api(tags = "PC端 - 授权相关接口", description = "若依原接口前端无需关注")
public class PcTokenController
{
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private InspectorTokenService inspectorTokenService;

    @PostMapping("login/app")
    public R<?> loginApp(@RequestBody LoginBody form)
    {
        // 用户登录
        LoginUser userInfo = sysLoginService.login(form.getUsername(), form.getPassword());
        Set<String> roles = userInfo.getRoles();
        if (!roles.contains("巡检员") && !roles.contains("合作商")) {
            Set<String> permissions = userInfo.getPermissions();
            //是否具备登录app端的权限符
            if(!permissions.contains("sys:app:login")){
                throw new ServiceException("用户密码不在指定范围");
            }
        }
        // 获取登录token
        return R.ok(inspectorTokenService.createToken(userInfo));
    }

    @PostMapping("login")
    public R<?> login(@RequestBody LoginBody form)
    {
        // 用户登录
        LoginUser userInfo = sysLoginService.login(form.getUsername(), form.getPassword());
        // 获取登录token
        return R.ok(tokenService.createToken(userInfo));
    }

    @DeleteMapping("logout")
    public R<?> logout(HttpServletRequest request)
    {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return R.ok();
    }

    @PostMapping("refresh")
    public R<?> refresh(HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return R.ok();
        }
        return R.ok();
    }

    @PostMapping("register")
    public R<?> register(@RequestBody RegisterBody registerBody)
    {
        // 用户注册
        sysLoginService.register(registerBody.getUsername(), registerBody.getPassword());
        return R.ok();
    }
}
