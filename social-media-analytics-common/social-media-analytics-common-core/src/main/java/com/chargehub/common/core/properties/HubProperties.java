package com.chargehub.common.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhanghaowei
 * @date 2025/06/07 15:01
 */
@Data
@Configuration
@ConfigurationProperties("hub")
public class HubProperties {

    private long appExpireDuration = 10080;

    private boolean standalone = false;


    private String authService = "admin";

    private String systemService = "admin";

    private String thirdService = "admin";

    private String fileService = "admin";

    private String jobService = "admin";

    private String logService = "admin";

    private Map<String, SocialMediaDataApi> socialMediaDataApi = new HashMap<>();

    private long recentDays;

    private Integer updateMinutes;

    public boolean headless = false;

    @Data
    public static class SocialMediaDataApi {

        private String host;

        private String token;
    }

    public boolean isValidDate(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        long daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return recentDays >= daysDiff;
    }


}
