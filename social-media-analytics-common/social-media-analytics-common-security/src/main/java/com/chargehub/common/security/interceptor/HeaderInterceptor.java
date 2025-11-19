package com.chargehub.common.security.interceptor;

import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.core.constant.SecurityConstants;
import com.chargehub.common.core.context.SecurityContextHolder;
import com.chargehub.common.core.exception.auth.NotLoginException;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.core.utils.JwtUtils;
import com.chargehub.common.core.utils.StringUtils;
import com.chargehub.common.security.auth.AuthUtil;
import com.chargehub.common.security.utils.SecurityUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author ruoyi
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor {

    private final HubProperties hubProperties;

    public HeaderInterceptor(HubProperties hubProperties) {
        this.hubProperties = hubProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

//        String loginType = ServletUtils.getHeader(request, SecurityConstants.LOGIN_USER_TYPE);
//        if(StringUtils.isNotEmpty(loginType)  && StringUtils.equals(SecurityConstants.LOGIN_USER_TYPE_APP, loginType)){
//            return true;
//        }
        String token = SecurityUtils.getToken();
        if (StringUtils.isNotEmpty(token)) {
            LoginUser loginUser = AuthUtil.getLoginUser(token);
            if (StringUtils.isNotNull(loginUser)) {
                AuthUtil.verifyLoginUserExpire(loginUser);
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
                SecurityContextHolder.setUserId(loginUser.getUserid() + "");
                SecurityContextHolder.setUserName(loginUser.getUsername());
                SecurityContextHolder.setUserKey(JwtUtils.getUserKey(token));
            }else{
                if(hubProperties.isStandalone()){
                    throw new NotLoginException("登录过期");
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        SecurityContextHolder.remove();
    }
}
