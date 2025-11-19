package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vchaoxi.entity.VcAgentWx;
import com.vchaoxi.vo.AgentWxVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 代理商相关 - 微信配置表 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface VcAgentWxMapper extends BaseMapper<VcAgentWx> {




    /**
     * 通过代理商id查询代理商微信配置信息
     * @param agentId
     * @return
     */
    public AgentWxVo selectByAgentId(@Param("agentId") Integer agentId);
}
