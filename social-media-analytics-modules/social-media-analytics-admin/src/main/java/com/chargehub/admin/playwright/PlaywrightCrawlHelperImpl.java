package com.chargehub.admin.playwright;

import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.mapper.SocialMediaAccountMapper;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Service
public class PlaywrightCrawlHelperImpl implements PlaywrightCrawlHelper {

    @Autowired
    private SocialMediaAccountMapper socialMediaAccountMapper;

    @Autowired
    private RedisService redisService;

    public PlaywrightCrawlHelperImpl(HubProperties hubProperties) {
        boolean headless = hubProperties.isHeadless();
        PlaywrightBrowser.setHeadless(headless);
        //项目启动时初始化playwright组件
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser("")) {
            //nothing to do
        }
    }

    @Override
    public void saveSmsCode(String accountId, String smsCode) {
        redisService.setCacheObject("craw:sms:code:" + accountId, smsCode);
    }

    @Override
    public String checkSmsCode(String accountId) {
        return redisService.getCacheObject("craw:sms:code:" + accountId);
    }

    @Override
    public void saveLoginQrCode(String accountId, String src) {
        redisService.setCacheObject("craw:sms:qr-code:" + accountId, src);
    }

    @Override
    public String getLoginQrCode(String accountId) {
        return redisService.getCacheObject("craw:sms:qr-code:" + accountId);
    }

    @Override
    public void saveLoginState(String accountId, String content) {
        socialMediaAccountMapper.lambdaUpdate()
                .set(SocialMediaAccount::getStorageState, content)
                .eq(SocialMediaAccount::getId, accountId)
                .update();
    }

    @Override
    public String getLoginState(String accountId) {
        SocialMediaAccount mediaAccount = socialMediaAccountMapper.lambdaQuery().select(SocialMediaAccount::getStorageState).eq(SocialMediaAccount::getId, accountId).one();
        if (mediaAccount == null) {
            return null;
        }
        return mediaAccount.getStorageState();
    }

    @Override
    public String getCrawlerLoginState(String platform) {
        SocialMediaAccount mediaAccount = this.socialMediaAccountMapper.lambdaQuery().select(SocialMediaAccount::getStorageState).eq(SocialMediaAccount::getPlatformId, platform)
                .eq(SocialMediaAccount::getCrawler, 1).one();
        if (mediaAccount == null) {
            return null;
        }
        return mediaAccount.getStorageState();
    }

    @Override
    public void updateCrawlerLoginState(String platform, String content) {
        if (StringUtils.isNotBlank(content)) {
            //此处不再更新cookie，只有为空的时候更新
            return;
        }
        this.socialMediaAccountMapper.lambdaUpdate()
                .set(SocialMediaAccount::getStorageState, content)
                .eq(SocialMediaAccount::getPlatformId, platform)
                .eq(SocialMediaAccount::getCrawler, 1)
                .update();
    }

    @Override
    public Map<String, String> getCrawlerLoginStateMap() {
        List<SocialMediaAccount> list = this.socialMediaAccountMapper.lambdaQuery().eq(SocialMediaAccount::getCrawler, 1).list();
        Map<String, String> map = new HashMap<>();
        for (SocialMediaAccount mediaAccount : list) {
            map.put(mediaAccount.getPlatformId(), mediaAccount.getStorageState());
        }
        return map;
    }
}
