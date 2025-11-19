package com.chargehub.common.security.aspect;

import com.chargehub.common.security.annotation.RequiresLogin;
import com.chargehub.common.security.annotation.RequiresPermissions;
import com.chargehub.common.security.annotation.RequiresRoles;
import com.chargehub.common.security.auth.AuthUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 基于 Spring Aop 的注解鉴权
 *
 * @author kong
 */
@Aspect
@Component
public class PreAuthorizeAspect {
    /**
     * 构建
     */
    public PreAuthorizeAspect() {
    }

    /**
     * 定义AOP签名 (切入所有使用鉴权注解的方法)
     */
    public static final String POINTCUT_SIGN = " @annotation(com.chargehub.common.security.annotation.RequiresLogin) || "
            + "@annotation(com.chargehub.common.security.annotation.RequiresPermissions) || "
            + "@annotation(com.chargehub.common.security.annotation.RequiresRoles)";

    @Autowired(required = false)
    private List<AuthenticationLoginInterceptor> authenticationLoginInterceptors;

    /**
     * 声明AOP签名
     */
    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {
    }

    /**
     * 环绕切入
     *
     * @param joinPoint 切面对象
     * @return 底层方法执行后的返回值
     * @throws Throwable 底层方法抛出的异常
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 注解鉴权
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        checkMethodAnnotation(signature.getMethod());
        try {
            // 执行原有逻辑
            Object obj = joinPoint.proceed();
            return obj;
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * 对一个Method对象进行注解检查
     */
    public void checkMethodAnnotation(Method method) {
        // 校验 @RequiresLogin 注解
        RequiresLogin requiresLogin = method.getAnnotation(RequiresLogin.class);
        if (requiresLogin != null) {
            AuthUtil.checkLogin();
            if (requiresLogin.doIntercept()) {
                if (authenticationLoginInterceptors != null) {
                    for (AuthenticationLoginInterceptor authenticationLoginInterceptor : authenticationLoginInterceptors) {
                        authenticationLoginInterceptor.afterAuthentication();
                    }
                }
            }
        }

        // 校验 @RequiresRoles 注解
        RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
        if (requiresRoles != null) {
            AuthUtil.checkRole(requiresRoles);
        }

        // 校验 @RequiresPermissions 注解
        RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
        if (requiresPermissions != null) {
            AuthUtil.checkPermi(requiresPermissions);
        }
    }
}
