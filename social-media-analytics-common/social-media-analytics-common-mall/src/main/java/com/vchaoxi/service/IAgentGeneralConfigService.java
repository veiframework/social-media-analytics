package com.vchaoxi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vchaoxi.entity.AgentGeneralConfig;
import com.vchaoxi.param.AgentGeneralConfigParams;
import com.vchaoxi.vo.AgentGeneralConfigVo;
import com.vchaoxi.vo.CommonResult;

/**
 * <p>
 * 通用配置信息字段表 服务类
 * </p>
 *
 * @author hanfuxian
 * @since 2024-08-05
 */
public interface IAgentGeneralConfigService extends IService<AgentGeneralConfig> {

    /**
     * 线下核销信息列表
     * @param page
     * @param phone
     * @return
     */
    CommonResult getGeneralConfigList(Page<AgentGeneralConfigVo> page, String address, String phone, String name);

    /**
     * 线下核销信息详情
     * @param configId
     * @return
     */
    CommonResult getGeneralConfigInfo(Integer configId);

    /**
     * 添加线下核销信息
     * @param configParams
     * @return
     */
    CommonResult addGeneralConfig(AgentGeneralConfigParams configParams);

    /**
     * 更新核销信息
     * @param configParams
     * @return
     */
    CommonResult updateGeneralConfig(AgentGeneralConfigParams configParams);

    /**
     * 删除核销信息
     * @param configId
     * @return
     */
    CommonResult delGeneralConfig(Integer configId);
}
