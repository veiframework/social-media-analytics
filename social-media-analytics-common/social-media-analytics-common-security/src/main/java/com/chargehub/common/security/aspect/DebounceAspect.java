package com.chargehub.common.security.aspect;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.chargehub.common.security.annotation.Debounce;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/05/24 12:59
 */
@Aspect
@Component
public class DebounceAspect implements Ordered {

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    private final TimedCache<String, String> debounceCache = new TimedCache<>(3000);

    @Around("@annotation(debounce)")
    public Object innerAround(ProceedingJoinPoint point, Debounce debounce) throws Throwable {
        Method method = getMethod(point);
        Map<String, Object> parameter = getParameterMap(method, point.getArgs());
        String methodName = point.getTarget().getClass().getName() + method.getName();
        String requestId = methodName + JSON.toJSONString(parameter);
        boolean result = debounce(requestId, debounce);
        Assert.isTrue(result, "操作过于频繁,请稍后再试!");
        return point.proceed();
    }

    public synchronized boolean debounce(String requestId, Debounce debounce) {
        int duration = debounce.duration();
        if (redisTemplate != null) {
            return redisTemplate.opsForValue().setIfAbsent(requestId, null, Duration.of(duration, ChronoUnit.MILLIS));
        }
        boolean containsKey = debounceCache.containsKey(requestId);
        if (containsKey) {
            return false;
        }
        debounceCache.put(requestId, null, duration);
        return true;
    }


    public Map<String, Object> getParameterMap(Method method, Object[] args) {
        Map<String, Object> map = new HashMap<>(16);
        if (args == null) {
            return map;
        }
        if (args.length == 1) {
            Map<String, Object> stringObjectMap = BeanUtil.beanToMap(args[0]);
            if (MapUtil.isNotEmpty(stringObjectMap)) {
                return stringObjectMap;
            }
        }
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            String parameterName = parameter.getName();
            map.put(parameterName, args[i]);
        }
        return map;
    }

    public Method getMethod(JoinPoint joinPoint) throws IllegalAccessException, NoSuchMethodException {
        final Signature sig = joinPoint.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalAccessException("This annotation is only valid on a method.");
        }
        final MethodSignature msig = (MethodSignature) sig;
        final Object target = joinPoint.getTarget();

        String name = msig.getName();
        Class<?>[] parameters = msig.getParameterTypes();

        return target.getClass().getMethod(name, parameters);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 1;
    }
}
