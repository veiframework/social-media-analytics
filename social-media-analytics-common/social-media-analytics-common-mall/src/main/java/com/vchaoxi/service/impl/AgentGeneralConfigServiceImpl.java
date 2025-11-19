package com.vchaoxi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vchaoxi.entity.AgentGeneralConfig;
import com.vchaoxi.mapper.AgentGeneralConfigMapper;
import com.vchaoxi.param.AgentGeneralConfigParams;
import com.vchaoxi.service.IAgentGeneralConfigService;
import com.vchaoxi.vo.AgentGeneralConfigVo;
import com.vchaoxi.vo.CommonResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 通用配置信息字段表 服务实现类
 * </p>
 *
 * @author hanfuxian
 * @since 2024-08-05
 */
@Service
public class AgentGeneralConfigServiceImpl extends ServiceImpl<AgentGeneralConfigMapper, AgentGeneralConfig> implements IAgentGeneralConfigService {

    @Autowired
    private AgentGeneralConfigMapper configMapper;

    /**
     * 线下核销信息列表
     *
     * @param page
     * @param address 地址
     * @param phone   电话
     * @param name    联系人
     * @return
     */
    @Override
    public CommonResult getGeneralConfigList(Page<AgentGeneralConfigVo> page, String address, String phone, String name) {
        List<AgentGeneralConfigVo> configVos = configMapper.getGeneralConfigList(page, address, phone, name);
        return CommonResult.success(page.setRecords(configVos));
    }

    /**
     * 线下核销信息详情
     *
     * @param configId
     * @return
     */
    @Override
    public CommonResult getGeneralConfigInfo(Integer configId) {
        AgentGeneralConfig agentGeneralConfigDb = configMapper.selectOne(new LambdaQueryWrapper<AgentGeneralConfig>().eq(AgentGeneralConfig::getIsDelete, 0).eq(AgentGeneralConfig::getId, configId));
        if (Objects.isNull(agentGeneralConfigDb)) {
            return CommonResult.error("核销信息不存在，请稍后再试");
        }
        return CommonResult.success(agentGeneralConfigDb);
    }

    /**
     * 添加线下核销信息
     *
     * @param configParams
     * @return
     */
    @Override
    public CommonResult addGeneralConfig(AgentGeneralConfigParams configParams) {
        AgentGeneralConfig agentGeneralConfig = new AgentGeneralConfig();
        BeanUtils.copyProperties(configParams, agentGeneralConfig);
        configMapper.insert(agentGeneralConfig);
        return CommonResult.success("添加成功");
    }

    /**
     * 更新核销信息
     *
     * @param configParams
     * @return
     */
    @Override
    public CommonResult updateGeneralConfig(AgentGeneralConfigParams configParams) {
        AgentGeneralConfig agentGeneralConfigDb = configMapper.selectOne(new LambdaQueryWrapper<AgentGeneralConfig>()
                .eq(AgentGeneralConfig::getIsDelete, 0).eq(AgentGeneralConfig::getId, configParams.getId()));
        if (Objects.isNull(agentGeneralConfigDb)) {
            return CommonResult.error("核销信息不存在，请稍后再试");
        }
        BeanUtils.copyProperties(configParams, agentGeneralConfigDb);
        configMapper.updateById(agentGeneralConfigDb);
        return CommonResult.success("操作成功");
    }

    /**
     * 删除核销信息
     *
     * @param configId
     * @return
     */
    @Override
    public CommonResult delGeneralConfig(Integer configId) {
        AgentGeneralConfig agentGeneralConfigDb = configMapper.selectOne(new LambdaQueryWrapper<AgentGeneralConfig>()
                .eq(AgentGeneralConfig::getIsDelete, 0).eq(AgentGeneralConfig::getId, configId));
        if (Objects.isNull(agentGeneralConfigDb)) {
            return CommonResult.error("核销信息不存在，请稍后再试");
        }
        agentGeneralConfigDb.setIsDelete(1);
        configMapper.updateById(agentGeneralConfigDb);
        return CommonResult.success("操作成功");
    }
}
