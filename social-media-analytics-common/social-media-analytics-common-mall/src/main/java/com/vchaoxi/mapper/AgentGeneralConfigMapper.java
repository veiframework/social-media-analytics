package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.entity.AgentGeneralConfig;
import com.vchaoxi.vo.AgentGeneralConfigVo;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * 通用配置信息字段表 Mapper 接口
 * </p>
 *
 * @author hanfuxian
 * @since 2024-08-05
 */
public interface AgentGeneralConfigMapper extends BaseMapper<AgentGeneralConfig> {

    /**
     * 核销地址列表
     *
     * @param page
     * @param address
     * @param phone
     * @param name
     * @return
     */
    List<AgentGeneralConfigVo> getGeneralConfigList(Page<AgentGeneralConfigVo> page,
                                                    @Param("address") String address,
                                                    @Param("phone") String phone,
                                                    @Param("name") String name);
}
