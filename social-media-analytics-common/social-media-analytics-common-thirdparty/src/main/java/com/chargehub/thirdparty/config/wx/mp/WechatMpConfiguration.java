package com.chargehub.thirdparty.config.wx.mp;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:00
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.config.wx.mp
 * @Filename：WechatMpConfiguration
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WechatMpProperties.class)
public class WechatMpConfiguration {
    @Autowired
    private WechatMpProperties wechatMpProperties;

    @Bean
    public WxMpService wxMpService() {
        final List<WechatMpProperties.MpConfig> configs = wechatMpProperties.getConfigs();
        if (configs == null) {
            throw new RuntimeException("清先配置公众号参数");
        }

        WxMpService service = new WxMpServiceImpl();
        service.setMultiConfigStorages(configs
                .stream().map(a -> {
                    WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
                    configStorage.setAppId(a.getAppId());
                    configStorage.setSecret(a.getSecret());
                    configStorage.setToken(a.getToken());
                    configStorage.setAesKey(a.getAesKey());
                    return configStorage;
                }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o)));
        return service;
    }
}
