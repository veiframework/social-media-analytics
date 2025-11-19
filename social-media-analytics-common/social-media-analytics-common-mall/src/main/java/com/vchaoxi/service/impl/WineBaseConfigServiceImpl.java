package com.vchaoxi.service.impl;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vchaoxi.entity.WineBaseConfig;
import com.vchaoxi.mapper.WineBaseConfigMapper;
import com.vchaoxi.param.CommissionRateConfigDto;
import com.vchaoxi.service.IWineBaseConfigService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hanfuxian
 * @since 2024-08-05
 */
@Service
public class WineBaseConfigServiceImpl extends ServiceImpl<WineBaseConfigMapper, WineBaseConfig> implements IWineBaseConfigService {

    @Override
    public WineBaseConfig getCommissionConfig() {
        List<WineBaseConfig> list = this.lambdaQuery().list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void updateCommissionRate(CommissionRateConfigDto dto) {
        this.lambdaUpdate().remove();
        WineBaseConfig wineBaseConfig = new WineBaseConfig();
        wineBaseConfig.setCommissionRate(dto.getCommissionRate());
        wineBaseConfig.setInsertTime(LocalDateTime.now());
        wineBaseConfig.setUpdateTime(LocalDateTime.now());
        wineBaseConfig.setIsDelete(0);
        this.save(wineBaseConfig);
    }

}
