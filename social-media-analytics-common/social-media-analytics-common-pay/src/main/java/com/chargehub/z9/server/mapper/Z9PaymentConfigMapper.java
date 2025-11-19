package com.chargehub.z9.server.mapper;


import com.chargehub.common.security.template.mybatis.Z9MpCrudMapper;
import com.chargehub.z9.server.domain.Z9PaymentConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Zhanghaowei
 * @date 2025/05/19 17:05
 */
@Mapper
public interface Z9PaymentConfigMapper extends Z9MpCrudMapper<Z9PaymentConfig> {

    /**
     * 获取配置
     *
     * @param paymentChannel
     * @param tenantId
     * @return
     */
    default Z9PaymentConfig getConfigByTenantId(Integer paymentChannel, String tenantId) {
        return this.lambdaQuery()
                .eq(Z9PaymentConfig::getPaymentChannel, paymentChannel)
                .eq(Z9PaymentConfig::getTenantId, tenantId).one();
    }

}
