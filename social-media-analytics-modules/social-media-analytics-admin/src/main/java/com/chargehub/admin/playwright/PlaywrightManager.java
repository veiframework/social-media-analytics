package com.chargehub.admin.playwright;

import cn.hutool.core.thread.ThreadUtil;
import com.chargehub.common.core.utils.StringUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 代理服务器      String douyin = "https://proxy.scdn.io";
 *
 * @author Zhanghaowei
 * @since 2025-11-29 10:36
 */
@Slf4j
public class PlaywrightManager {

    public static final PlaywrightCrawlHelper SOCIAL_MEDIA_CRAWL_HELPER = new PlaywrightCrawlTestHelper();

    public static boolean smsLogin = true;

    public static void loginWebDouYin() {
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser("15514557561", "qwe6181513640031", null)) {
            playwrightBrowser.init();
            Page page = playwrightBrowser.newPage();
            page.navigate("https://www.douyin.com", new Page.NavigateOptions().setTimeout(220_000));
            log.info("进入页面");
            page.waitForSelector("input[placeholder='请输入手机号']");
            if (smsLogin) {
                page.click("text=验证码登录");
            } else {
                page.click("text=密码登录");
            }
            log.info("检测到输入手机号");
            page.fill("input[placeholder='请输入手机号']", playwrightBrowser.getUsername());
            log.info("手机号填充完毕");
            if (smsLogin) {
                page.click("text=获取验证码");
                log.info("点击获取验证码,等待...");
                String smsCode = "";
                while (StringUtils.isBlank(smsCode) && smsCode.length() != 6) {
                    smsCode = SOCIAL_MEDIA_CRAWL_HELPER.checkSmsCode("");
                    ThreadUtil.safeSleep(100);
                }
                log.info("填充验证码{}", smsCode);
                page.fill("input[placeholder='请输入验证码']", smsCode);
            } else {
                log.info("填充密码");
                page.fill("input[placeholder='请输入密码']", playwrightBrowser.getPassword());
            }
            log.info("发出登陆请求");
            page.click("text=登录/注册");
            page.waitForSelector("text=使用原设备扫码");
            log.info("等待二维码");
            page.waitForSelector("img[aria-label='二维码']", new Page.WaitForSelectorOptions()
                    .setTimeout(60_000));

            // 获取 img 的 src 属性（Base64 字符串）
            String src = page.getAttribute("img[aria-label='二维码']", "src");
            SOCIAL_MEDIA_CRAWL_HELPER.saveLoginQrCode("", src);
            // 提取 Base64 部分（去掉 data:image/jpeg;base64, 前缀）
            page.waitForSelector("span[data-e2e='live-avatar']",
                    new Page.WaitForSelectorOptions().setTimeout(2200_000));
            log.info("登陆成功");
            // 保存登录状态到文件（包含 cookies + localStorage）
            String storageState = page.context().storageState();
            SOCIAL_MEDIA_CRAWL_HELPER.saveLoginState("", storageState);
        }
    }

    public static String crawlDouYinWorkList(String enterUrl, String loginState, Consumer<String> consumer) {
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(loginState)) {
            playwrightBrowser.init();
            Page page = playwrightBrowser.newPage();
            page.onResponse(res -> {
                res.headersArray().stream().filter(h -> "set-cookie".equalsIgnoreCase(h.name)).map(i -> i.value)
                        .findFirst().ifPresent(cookie -> log.debug("收到 Set-Cookie: " + cookie));
                if (res.url().contains("/aweme/v1/web/aweme/post/")) {
                    consumer.accept(new String(res.body()));
                }
            });
            page.navigate(enterUrl, new Page.NavigateOptions().setTimeout(120_000));
            boolean visible = page.isVisible("text=登录后免费畅享高清视频");
            if (visible) {
                log.error("需要重新登陆了");
                return null;
            }
            log.info("开始滚动加载数据");
            for (int i = 0; i < 99999; i++) {
                if (page.isVisible("text=暂时没有更多了")) {
                    break;
                }
                page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
                page.waitForTimeout(200);
            }
            return page.context().storageState();
        }
    }


    public static void main(String[] args) {
        String douyin = "https://www.douyin.com/user/MS4wLjABAAAAhSaD3wD3rsLVCezq2LaPpCXrRDjBb8R8Np4SnAcZQE4";

//                loginWebDouYin();

        String loginState = SOCIAL_MEDIA_CRAWL_HELPER.getLoginState("");
        List<JsonNode> list = new CopyOnWriteArrayList<>();
        String newLoginState = crawlDouYinWorkList(douyin, loginState, body -> {
            log.info("拦截到数据");
            list.add(JacksonUtil.toObj(body));
        });
        SOCIAL_MEDIA_CRAWL_HELPER.saveLoginState("", newLoginState);

    }


}
