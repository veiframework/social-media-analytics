package com.chargehub.admin.enums;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

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
    BILI_BILI("bilibili", uri -> DOU_YIN.getSecUserId().apply(uri));

    private final String domain;


    private final Function<URI, String> secUserId;

    SocialMediaPlatformEnum(String domain, Function<URI, String> secUserId) {
        this.domain = domain;
        this.secUserId = secUserId;
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


    public static void main(String[] args) {
        SecUser redNote = SocialMediaPlatformEnum.parseSecUserId("https://xhslink.com/m/4LAAkBKkwiX");
        System.out.println(redNote);
        SecUser douyin = SocialMediaPlatformEnum.parseSecUserId("https://v.douyin.com/mHR6pDL6FEg/ 0@1.com :1pm");
        System.out.println(douyin);
        SecUser b = SocialMediaPlatformEnum.parseSecUserId("https://b23.tv/BBkZ3lo");
        System.out.println(b);

    }

}
