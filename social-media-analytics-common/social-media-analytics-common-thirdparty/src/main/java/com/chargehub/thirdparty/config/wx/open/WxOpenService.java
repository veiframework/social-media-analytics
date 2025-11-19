package com.chargehub.thirdparty.config.wx.open;

import me.chanjar.weixin.open.api.impl.WxOpenInRedissonConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 13:57
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.config.wx.open
 * @Filename：WxOpenService
 */
@Service
public class WxOpenService extends WxOpenServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(WxOpenService.class);


    @Autowired
    private RedissonClient redissonClient;
    private WxOpenMessageRouter wxOpenMessageRouter;

    @Value("${wechat.open.appId}")
    private String appId;
    @Value("${wechat.open.secret}")
    private String secret;
    @Value("${wechat.open.token}")
    private String token;
    @Value("${wechat.open.aesKey}")
    private String aesKey;




    @PostConstruct
    public void init() {
        WxOpenInRedissonConfigStorage inRedisConfigStorage = new WxOpenInRedissonConfigStorage(redissonClient);
        inRedisConfigStorage.setComponentAppId(appId);
        inRedisConfigStorage.setComponentAppSecret(secret);
        inRedisConfigStorage.setComponentToken(token);
        inRedisConfigStorage.setComponentAesKey(aesKey);
        setWxOpenConfigStorage(inRedisConfigStorage);
        wxOpenMessageRouter = new WxOpenMessageRouter(this);
        wxOpenMessageRouter
                .rule()
                .handler((wxMpXmlMessage, map, wxMpService, wxSessionManager) -> {
                    log.info("\n接收到 {} 公众号请求消息，内容：{}", wxMpService.getWxMpConfigStorage().getAppId(), wxMpXmlMessage);
                    return null;
                }).next()

                .rule()
                .msgType("event")
                .event("");
    }
    public WxOpenMessageRouter getWxOpenMessageRouter(){
        return wxOpenMessageRouter;
    }
}
