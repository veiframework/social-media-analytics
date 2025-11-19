package com.chargehub.thirdparty.api.config;

import com.chargehub.thirdparty.api.domain.dto.amap.AliMapChargeCrypto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.yaml.snakeyaml.util.UriEncoder;

import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 15:13
 */
@Slf4j
public class AliMapFeignConfig {

    private final ObjectMapper objectMapper;


    public AliMapFeignConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    private ChargehubAmapProperties amapProperties;


    @Order
    @Bean
    public RequestInterceptor aliMapChargeRequestInterceptor(AliMapChargeCrypto aliMapChargeCrypto) {
        return requestTemplate -> {
            requestTemplate.removeHeader(HttpHeaders.AUTHORIZATION);
            byte[] originBody = requestTemplate.body();
            try {
                byte[] value = aliMapChargeCrypto.encrypt(originBody);
                Map<String, String> readValue = objectMapper.readValue(value, new TypeReference<Map<String, String>>() {
                });
                StringBuilder encodedBody = new StringBuilder();
                readValue.forEach((k, v) -> {
                    if (encodedBody.length() > 0) {
                        encodedBody.append("&");
                    }
                    String key = UriEncoder.encode(k);
                    String val = UriEncoder.encode(v);
                    encodedBody.append(key).append("=").append(val);
                });
                requestTemplate.body(encodedBody.toString());
                log.info("推送高德数据{}:\\\n{}", requestTemplate.url(), objectMapper.writeValueAsString(readValue));
            } catch (Exception e) {
                log.error("高德推送失败", e);
            }
        };
    }

}
