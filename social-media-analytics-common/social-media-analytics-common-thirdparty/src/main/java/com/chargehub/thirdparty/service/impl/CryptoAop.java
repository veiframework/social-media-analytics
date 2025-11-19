package com.chargehub.thirdparty.service.impl;

import cn.hutool.core.lang.Assert;
import com.chargehub.thirdparty.api.CryptoService;
import com.chargehub.thirdparty.api.annotation.Crypto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/08/06 16:09
 */
@Component
@Aspect
@Slf4j
public class CryptoAop {

    @Autowired
    private Map<String, CryptoService> cryptoServiceMap;


    @Around("@annotation(crypto)")
    public Object innerAround(ProceedingJoinPoint point, Crypto crypto) throws Throwable {
        String name = crypto.name();
        Assert.notBlank(name, "加解密实例不得为空");
        this.cryptoServiceMap.get(name).decrypt();
        return point.proceed();
    }


}
