package com.chargehub.admin.playwright;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.function.Consumer;

/**
 * 代理服务器      String douyin = "https://proxy.scdn.io";
 *
 * @author Zhanghaowei
 * @since 2025-11-29 10:36
 */
@Service
@Slf4j
public class PlaywrightManager {

    @Autowired
    private PlaywrightCrawlHelper playwrightCrawlHelper;


    private static final boolean SMS_LOGIN = true;

    public void loginWebDouYin() {
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser("15514557561", "qwe6181513640031", null)) {
            Page page = playwrightBrowser.newPage();
            page.navigate("https://www.douyin.com", new Page.NavigateOptions().setTimeout(60_000));
            log.info("进入页面");
            page.waitForSelector("input[placeholder='请输入手机号']");
            if (SMS_LOGIN) {
                page.click("text=验证码登录");
            } else {
                page.click("text=密码登录");
            }
            log.info("检测到输入手机号");
            page.fill("input[placeholder='请输入手机号']", playwrightBrowser.getUsername());
            log.info("手机号填充完毕");
            if (SMS_LOGIN) {
                page.click("text=获取验证码");
                log.info("点击获取验证码,等待...");
                String smsCode = "";
                for (int i = 0; i < 6000; i++) {
                    smsCode = playwrightCrawlHelper.checkSmsCode("");
                    if (StringUtils.isNotBlank(smsCode)) {
                        break;
                    }
                    ThreadUtil.safeSleep(10);
                }
                log.info("填充验证码{}", smsCode);
                page.fill("input[placeholder='请输入验证码']", smsCode);
            } else {
                log.info("填充密码");
                page.fill("input[placeholder='请输入密码']", playwrightBrowser.getPassword());
            }
            log.info("发出登陆请求");
            //TODO 此处频繁登录有可能触发图形验证码
            page.click("text=登录/注册");
            Response smsResponse = page.waitForResponse(res -> res.url().contains("sms_login"), new Page.WaitForResponseOptions().setTimeout(60_000), () -> {
            });
            String responseBody = new String(smsResponse.body());
            JsonNode jsonNode = JacksonUtil.toObj(responseBody);
            if (jsonNode.at("/data/description").asText().contains("错误")) {
                throw new IllegalArgumentException("登录失败,验证码或手机号不正确!");
            }
            if (page.isVisible("text=使用原设备扫码")) {
                page.waitForSelector("text=使用原设备扫码", new Page.WaitForSelectorOptions().setTimeout(60_000));
                log.info("等待二维码");
                page.waitForSelector("img[aria-label='二维码']", new Page.WaitForSelectorOptions()
                        .setTimeout(60_000));

                // 获取 img 的 src 属性（Base64 字符串）
                String src = page.getAttribute("img[aria-label='二维码']", "src");
                playwrightCrawlHelper.saveLoginQrCode("", src);
            }
            page.waitForSelector("span[data-e2e='live-avatar']",
                    new Page.WaitForSelectorOptions().setTimeout(60_000));
            log.info("登陆成功");
            // 保存登录状态到文件（包含 cookies + localStorage）
            String storageState = page.context().storageState();
            playwrightCrawlHelper.saveLoginState("", storageState);
        }
    }

    public static void crawlRedNoteLogin() {
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(new PlaywrightCrawlTestHelper().getLoginState(""))) {
            Page page = playwrightBrowser.newPage();
            page.navigate("https://www.xiaohongshu.com/", new Page.NavigateOptions().setTimeout(60_000));
            log.info("进入页面");
            page.waitForSelector("text='登录后推荐更懂你的笔记'");
            ElementHandle element = page.querySelector(".qrcode-img");
            String src = element.getAttribute("src");
            System.out.println(src);
            page.waitForSelector("a[title='我']", new Page.WaitForSelectorOptions().setTimeout(60_000));
            String storageState = page.context().storageState();
            FileUtil.writeUtf8String(storageState, "E:\\workspace\\social-media-analytics\\social-media-analytics-modules\\social-media-analytics-admin\\src\\main\\resources\\red_note_state.json");
        }
    }

    public static String crawlDouYinWorkList(String enterUrl, String loginState, Consumer<String> consumer) {
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(loginState)) {
            Page page = playwrightBrowser.newPage();
            page.onResponse(res -> {
                if (res.url().contains("/aweme/v1/web/aweme/post/")) {
                    consumer.accept(new String(res.body()));
                }
            });
            page.navigate(enterUrl, new Page.NavigateOptions().setTimeout(60_000));
            boolean visible = page.isVisible("text=登录");
            if (visible) {
                log.error("需要重新登陆了");
                return null;
            }
            log.info("开始滚动加载数据");
            for (int i = 0; i < 6000; i++) {
                if (page.isVisible("text=暂时没有更多了")) {
                    break;
                }
                page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
                page.waitForTimeout(10);
            }
            return page.context().storageState();
        }
    }

    public static SocialMediaWork getWork(String workUrl) {

        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(new PlaywrightCrawlTestHelper().getLoginState(""))) {
            Page page = playwrightBrowser.newPage();
            Response response = page.waitForResponse(res -> res.url().contains("/creator/item/mget"), new Page.WaitForResponseOptions().setTimeout(60_000), () -> {
                page.navigate(workUrl, new Page.NavigateOptions().setTimeout(60_000));
            });
            Assert.isTrue(response.ok());
            String body = new String(response.body());
            System.out.println(body);
            return null;
        }
    }




    public static void main(String[] args) {
        String douyin = "https://www.douyin.com/user/MS4wLjABAAAAhSaD3wD3rsLVCezq2LaPpCXrRDjBb8R8Np4SnAcZQE4";
        String realUrl = "https://www.douyin.com/video/7577294438381237403";
        String shareUrl = "https://v.douyin.com/BnP9rp9WtMw/ 11/28 nqR:/ x@s.eO";
        String xiaohongshu = "http://xhslink.com/o/6vwGmnLDROY";
//        getWork("https://creator.douyin.com/creator-micro/work-management/work-detail/7578754998953051121");
        //        crawlRedNoteLogin();
    }


}
