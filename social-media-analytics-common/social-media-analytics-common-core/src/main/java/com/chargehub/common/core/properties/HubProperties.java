package com.chargehub.common.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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

    private SocialMediaDataApi socialMediaDataApi = new SocialMediaDataApi();

    @Data
    public static class SocialMediaDataApi {

        private String host;

        private String token;
    }

}
