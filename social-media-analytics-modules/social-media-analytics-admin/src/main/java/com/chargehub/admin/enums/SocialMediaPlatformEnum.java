package com.chargehub.admin.enums;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.playwright.BrowserConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.net.Proxy;
import java.net.URI;
import java.util.Arrays;
import java.util.function.Function;

/**
 * @author Zhanghaowei
 * @since 2025-11-17 8:01
 */
@Getter
public enum SocialMediaPlatformEnum {

    DOU_YIN("douyin", uri -> {
        String path = uri.getPath();
        String[] split = path.split("/");
        return split[split.length - 1];
    }),
    RED_NOTE("xiaohongshu", uri -> DOU_YIN.getSecUserId().apply(uri)),
    BILI_BILI("bilibili", uri -> DOU_YIN.getSecUserId().apply(uri)),
    WECHAT_VIDEO("wechatvideo", uri -> null),
    KUAI_SHOU("kuaishou", uri -> DOU_YIN.getSecUserId().apply(uri));

    private final String domain;


    private final Function<URI, String> secUserId;

    SocialMediaPlatformEnum(String domain, Function<URI, String> secUserId) {
        this.domain = domain;
        this.secUserId = secUserId;
    }

    public static PlatformExtra getPlatformByWorkUrl(String workUrl) {
        HttpRequest httpRequest = HttpUtil.createGet(workUrl);
        Proxy proxy = BrowserConfig.getProxy();
        try (HttpResponse execute = httpRequest.headerMap(BrowserConfig.BROWSER_HEADERS, true).setFollowRedirects(true).setProxy(proxy).execute()) {
            String location = httpRequest.getUrl();
            URI uri = URLUtil.toURI(location);
            String host = uri.getHost();
            SocialMediaPlatformEnum socialMediaPlatformEnum = Arrays.stream(values()).filter(i -> host.contains(i.domain) || workUrl.contains(i.domain)).findFirst().orElseThrow(() -> new IllegalArgumentException("不支持的平台类型"));
            return new PlatformExtra(location, socialMediaPlatformEnum);
        }
    }

    public static SocialMediaPlatformEnum getByDomain(String domain) {
        SocialMediaPlatformEnum socialMediaPlatformEnum = Arrays.stream(values()).filter(i -> i.domain.equals(domain)).findFirst().orElse(null);
        Assert.notNull(socialMediaPlatformEnum, "不支持的社交平台类型");
        return socialMediaPlatformEnum;
    }

    public static SecUser parseSecUserId(String shareLink) {
        String location;
        try (HttpResponse execute = HttpUtil.createGet(shareLink).execute()) {
            location = execute.header("Location");
        }
        if (StringUtils.isBlank(location)) {
            return null;
        }
        URI uri = URLUtil.toURI(location);
        String host = uri.getHost();
        SocialMediaPlatformEnum platformEnum = Arrays.stream(values()).filter(i -> host.contains(i.domain)).findFirst().orElseThrow(() -> new IllegalArgumentException("不支持的平台类型"));
        String resultSecUserId = platformEnum.getSecUserId().apply(uri);
        return new SecUser(resultSecUserId, platformEnum.getDomain(), platformEnum);
    }


    @Data
    public static class SecUser {

        @Schema(description = "第三方的secUid")
        private String id;

        @Schema(description = "平台, DOU_YIN,RED_NOTE,BILI_BILI")
        private String platform;

        private SocialMediaPlatformEnum platformEnum;

        public SecUser() {
        }

        public SecUser(String id, String platform, SocialMediaPlatformEnum platformEnum) {
            this.id = id;
            this.platform = platform;
            this.platformEnum = platformEnum;
        }
    }

    @Data
    public static class PlatformExtra {
        private String location;
        private SocialMediaPlatformEnum platformEnum;

        public PlatformExtra(SocialMediaPlatformEnum platformEnum) {
            this.platformEnum = platformEnum;
        }

        public PlatformExtra(String location, SocialMediaPlatformEnum platformEnum) {
            this.location = location;
            this.platformEnum = platformEnum;
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        Object redNote = SocialMediaPlatformEnum.getPlatformByWorkUrl("https://xhslink.com/m/4LAAkBKkwiX");
        System.out.println(redNote);
        Object douyin = SocialMediaPlatformEnum.getPlatformByWorkUrl("https://v.douyin.com/mHR6pDL6FEg/ 0@1.com :1pm");
        System.out.println(douyin);
        Object b = SocialMediaPlatformEnum.getPlatformByWorkUrl("https://b23.tv/BBkZ3lo");
        System.out.println(b);

    }

}
