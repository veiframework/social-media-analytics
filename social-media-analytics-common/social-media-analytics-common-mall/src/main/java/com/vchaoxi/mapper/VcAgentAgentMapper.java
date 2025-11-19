package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.entity.VcAgentAgent;
import com.vchaoxi.vo.AgentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 代理商相关 - 代理商表 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface VcAgentAgentMapper extends BaseMapper<VcAgentAgent> {


    /**
     * 管理员查询代理商列表
     * @param page
     * @param agentId
     * @param name
     * @param adminMobile
     * @return
     */
    public List<AgentVo> adminSelectList(Page<AgentVo> page, @Param("agentId") Integer agentId, @Param("name")String name, @Param("adminMobile") String adminMobile);



    /**
     * 查询全部代理商列表
     * @return
     */
    public List<AgentVo> all();



    /**
     * 查询代理商信息
     * @param agentId
     * @return
     */
    public AgentVo selectByAgentId(@Param("agentId")Integer agentId);


}
