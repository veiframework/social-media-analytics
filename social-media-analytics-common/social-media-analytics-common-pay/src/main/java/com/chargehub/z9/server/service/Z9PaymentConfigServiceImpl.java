package com.chargehub.z9.server.service;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.map.MapUtil;
import com.chargehub.common.security.template.dto.Z9CrudQueryDto;
import com.chargehub.common.security.template.service.AbstractZ9CrudServiceImpl;
import com.chargehub.payment.PaymentChannel;
import com.chargehub.payment.PaymentConstant;
import com.chargehub.payment.alipay.AliPaymentProperties;
import com.chargehub.payment.allinpay.AllInPaymentProperties;
import com.chargehub.payment.baidu.BaiduPaymentProperties;
import com.chargehub.payment.wechat.WechatPaymentProperties;
import com.chargehub.z9.server.domain.Z9PaymentConfig;
import com.chargehub.z9.server.dto.Z9PaymentConfigDto;
import com.chargehub.z9.server.dto.Z9PaymentConfigQueryDto;
import com.chargehub.z9.server.mapper.Z9PaymentConfigMapper;
import com.chargehub.z9.server.vo.Z9PaymentConfigVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/05/19 17:06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class Z9PaymentConfigServiceImpl extends AbstractZ9CrudServiceImpl<Z9PaymentConfigMapper, Z9PaymentConfig> implements Z9PaymentConfigService {

    protected static final Map<Class<?>, Integer> PAYMENT_CHANNEL_MAP = MapUtil.builder(new HashMap<Class<?>, Integer>())
            .put(AliPaymentProperties.class, PaymentChannel.ALIPAY.ordinal())
            .put(WechatPaymentProperties.class, PaymentChannel.WECHAT.ordinal())
            .put(AllInPaymentProperties.class, PaymentChannel.ALLINPAY.ordinal())
            .put(BaiduPaymentProperties.class, PaymentChannel.BAIDU.ordinal())
            .build();

    private final ObjectMapper objectMapper;

    public Z9PaymentConfigServiceImpl(Z9PaymentConfigMapper baseMapper, ObjectMapper objectMapper) {
        super(baseMapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public <R> R getConfigByExtendParam(Class<R> configClass, Map<String, String> params) {
        Integer paymentChannel = PAYMENT_CHANNEL_MAP.get(configClass);
        Assert.notNull(paymentChannel, "not found payment channel config");
        String tenantId = params.get(PaymentConstant.TENANT_ID);
        Z9PaymentConfig z9PaymentConfig = null;
        if (StringUtils.isNotBlank(tenantId)) {
            z9PaymentConfig = this.baseMapper.getConfigByTenantId(paymentChannel, tenantId);
        }
        String paymentConfigId = params.get(PaymentConstant.PAYMENT_CONFIG_ID);
        if (StringUtils.isNotBlank(paymentConfigId)) {
            z9PaymentConfig = this.baseMapper.doGetDetailById(paymentConfigId);
        }
        Assert.notNull(z9PaymentConfig, "can not found payment config!");
        ObjectNode content = z9PaymentConfig.getContent();
        content.put("id", z9PaymentConfig.getId());
        try {
            return objectMapper.readValue(content.toString(), configClass);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public <R> R getByConfigId(Class<R> configClass, String configId) {
        Z9PaymentConfig z9PaymentConfig = this.baseMapper.doGetDetailById(configId);
        Assert.notNull(z9PaymentConfig, "can not found payment config!");
        ObjectNode content = z9PaymentConfig.getContent();
        try {
            return objectMapper.readValue(content.toString(), configClass);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    @Override
    public IExcelDictHandler getDictHandler() {
        return null;
    }

    @Override
    public Class<?> doGetDtoClass() {
        return Z9PaymentConfigDto.class;
    }

    @Override
    public Class<?> doGetVoClass() {
        return Z9PaymentConfigVo.class;
    }

    @Override
    public Class<? extends Z9CrudQueryDto<Z9PaymentConfig>> queryDto() {
        return Z9PaymentConfigQueryDto.class;
    }

    @Override
    public String excelName() {
        return "支付配置列表";
    }


}
