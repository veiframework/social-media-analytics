package com.vchaoxi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vchaoxi.entity.VcAgentAgent;
import com.vchaoxi.param.AgentParam;
import com.vchaoxi.vo.CommonResult;


/**
 * <p>
 * 代理商相关 - 代理商表 服务类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface IVcAgentAgentService extends IService<VcAgentAgent> {



    /**
     * 添加代理商信息
     * @param agentParam
     * @return
     */
    public CommonResult add(AgentParam agentParam);


    /**
     * 编辑代理商信息
     * @param agentParam
     * @return
     */
    public CommonResult edit(AgentParam agentParam);


    /**
     * 删除代理商信息
     * @param agentParam
     * @return
     */
    public CommonResult del(AgentParam agentParam);
}
