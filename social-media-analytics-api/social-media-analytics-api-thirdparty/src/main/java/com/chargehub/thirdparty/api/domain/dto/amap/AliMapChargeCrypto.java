package com.chargehub.thirdparty.api.domain.dto.amap;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.chargehub.thirdparty.api.CryptoService;
import com.chargehub.thirdparty.api.config.ChargehubAmapProperties;
import com.chargehub.thirdparty.api.domain.dto.amap.dto.AliMapCommonDto;
import com.chargehub.thirdparty.api.domain.dto.amap.vo.AliMapCommonBodyVo;
import com.chargehub.thirdparty.api.domain.dto.amap.vo.AliMapCommonRespVo;
import com.chargehub.thirdparty.api.util.AliMapChargeSecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/08/05 13:53
 */
@Slf4j
public class AliMapChargeCrypto implements CryptoService {

    private final ChargehubAmapProperties amapProperties;

    private final ObjectMapper objectMapper;

    @Autowired
    private HttpServletRequest request;

    public AliMapChargeCrypto(ChargehubAmapProperties amapProperties,
                              ObjectMapper objectMapper) {
        this.amapProperties = amapProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] encrypt(Map<String, String> originBody) {
        try {
            String appId = amapProperties.getAppId();
            String merchantPrivateKey = amapProperties.getMerchantPrivateKey();
            AliMapCommonDto<?> value = BeanUtil.toBean(originBody, AliMapCommonDto.class);
            value.setCharset("UTF-8");
            value.setVersion("1.0");
            value.setUtc_timestamp(System.currentTimeMillis() + "");
            value.setApp_id(appId);
            String string = objectMapper.writeValueAsString(value);
            Map<String, String> stringStringMap = objectMapper.readValue(string, new TypeReference<Map<String, String>>() {
            });
            String sign = AliMapChargeSecurityUtil.generateSign(stringStringMap, merchantPrivateKey);
            value.setSign(sign);
            value.setSign_type("RSA2");

            return objectMapper.writeValueAsBytes(value);
        } catch (Exception e) {
            log.error("高德推送失败", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public byte[] encrypt(byte[] originBody) {
        try {
            String appId = amapProperties.getAppId();
            String merchantPrivateKey = amapProperties.getMerchantPrivateKey();
            AliMapCommonDto<?> value = objectMapper.readValue(originBody, AliMapCommonDto.class);
            value.setCharset("UTF-8");
            value.setVersion("1.0");
            value.setUtc_timestamp(System.currentTimeMillis() + "");
            value.setApp_id(appId);
            String string = objectMapper.writeValueAsString(value);
            Map<String, String> stringStringMap = objectMapper.readValue(string, new TypeReference<Map<String, String>>() {
            });
            String sign = AliMapChargeSecurityUtil.generateSign(stringStringMap, merchantPrivateKey);
            value.setSign(sign);
            value.setSign_type("RSA2");
            return objectMapper.writeValueAsBytes(value);
        } catch (Exception e) {
            log.error("高德推送失败", e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public byte[] decrypt(InputStream body) {
        try {
            Map<String, String> stringObjectMap = objectMapper.readValue(body, new TypeReference<Map<String, String>>() {
            });
            boolean checkSign = AliMapChargeSecurityUtil.checkSign(stringObjectMap, amapProperties.getAmapPublicKey());
            Assert.isTrue(checkSign, "加密验签失败");
            return objectMapper.writeValueAsBytes(stringObjectMap);
        } catch (Exception e) {
            log.error("高德验证签名失败", e);
            AliMapCommonBodyVo<Object> bodyVo = new AliMapCommonBodyVo<>();
            bodyVo.setCode("40004");
            bodyVo.setMsg(e.getMessage());
            try {
                return objectMapper.writeValueAsBytes(new AliMapCommonRespVo<>(bodyVo));
            } catch (JsonProcessingException jsonProcessingException) {
                return null;
            }
        }
    }

    @Override
    public void decrypt() {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> parameter = new HashMap<>(16);
        parameterMap.forEach((k, v) -> {
            if (ArrayUtils.isNotEmpty(v) && !"key".equals(k)) {
                parameter.put(k, v[0]);
            }
        });
        try {
            log.info("高德拉取数据:\\\n{}", objectMapper.writeValueAsString(parameter));
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(parameter));
            this.decrypt(byteArrayInputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
