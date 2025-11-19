package com.chargehub.thirdparty.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ChWoProperties ch外部工单
 * date: 2024/12
 *
 * @author <a href="13721682347@163.com">TiAmo</a>
 */
@Data
@ConfigurationProperties("chargehub.ch-wo")
public class ChWoProperties {

    private String host;

    private String publicKey;

    private String notificationUrl;

}
