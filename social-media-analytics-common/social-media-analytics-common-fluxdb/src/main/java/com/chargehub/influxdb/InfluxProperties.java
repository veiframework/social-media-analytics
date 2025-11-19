package com.chargehub.influxdb;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/10/11 14:20
 */
@Data
@Component
@ConfigurationProperties("influxdb")
public class InfluxProperties implements InitializingBean {

    private String url;

    private String org;

    private String token;

    private Map<String, Datasource> datasource = new HashMap<>(16);

    private boolean prod = false;


    @Data
    public static class Datasource {

        private String url;

        private String org;

        private String token;

        private String bucket;

        private String retention;

    }

    @Override
    public void afterPropertiesSet() {
        datasource.forEach((k, v) -> {
            String org0 = v.getOrg();
            String url0 = v.getUrl();
            String token0 = v.getToken();
            if (StringUtils.isBlank(org0)) {
                v.setOrg(this.org);
            }
            if (StringUtils.isBlank(url0)) {
                v.setUrl(this.url);
            }
            if (StringUtils.isBlank(token0)) {
                v.setToken(this.token);
            }
        });
    }

}
