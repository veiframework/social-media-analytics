package com.chargehub.common.security.aspect;

/**
 * @author Zhanghaowei
 * @date 2025/06/07 11:36
 */
public interface AuthenticationLoginInterceptor {

    /**
     * 认证登录后
     */
    void afterAuthentication();

}
