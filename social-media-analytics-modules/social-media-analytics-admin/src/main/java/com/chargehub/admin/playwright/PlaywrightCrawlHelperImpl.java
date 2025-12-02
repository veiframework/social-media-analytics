package com.chargehub.admin.playwright;

import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.admin.account.mapper.SocialMediaAccountMapper;
import com.chargehub.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Service
public class PlaywrightCrawlHelperImpl implements PlaywrightCrawlHelper {

    @Autowired
    private SocialMediaAccountMapper socialMediaAccountMapper;

    @Autowired
    private RedisService redisService;

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
}
