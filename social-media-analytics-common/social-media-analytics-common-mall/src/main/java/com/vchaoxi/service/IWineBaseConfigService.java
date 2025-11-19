package com.vchaoxi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vchaoxi.entity.WineBaseConfig;
import com.vchaoxi.param.CommissionRateConfigDto;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hanfuxian
 * @since 2024-08-05
 */
public interface IWineBaseConfigService extends IService<WineBaseConfig> {

    /**
     * 获取提成配置
     *
     * @return
     */
    WineBaseConfig getCommissionConfig();

    /**
     * 更新提成比率
     *
     * @param dto
     */
    void updateCommissionRate(CommissionRateConfigDto dto);

}
