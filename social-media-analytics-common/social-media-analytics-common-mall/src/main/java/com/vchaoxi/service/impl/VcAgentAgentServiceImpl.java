package com.vchaoxi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vchaoxi.entity.VcAgentAgent;
import com.vchaoxi.entity.VcAgentWx;
import com.vchaoxi.mapper.VcAgentAgentMapper;
import com.vchaoxi.param.AgentParam;
import com.vchaoxi.service.IVcAgentAgentService;
import com.vchaoxi.service.IVcAgentWxService;
import com.vchaoxi.vo.CommonResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 代理商相关 - 代理商表 服务实现类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Service
public class VcAgentAgentServiceImpl extends ServiceImpl<VcAgentAgentMapper, VcAgentAgent> implements IVcAgentAgentService {



    @Autowired
    private IVcAgentWxService vcAgentWxService;
//    @Autowired
//    private IVcAdminAdminService vcAdminAdminService;
//    @Autowired
//    private IVcAdminRoleService vcAdminRoleService;
//    @Autowired
//    private IVcAdminUserRoleService vcAdminUserRoleService;





    /**
     * 添加代理商
     * @param agentParam
     * @return
     */
    @Override
    @Transactional
    public CommonResult add(AgentParam agentParam) {
        //添加代理商信息
        VcAgentAgent vcAgentAgent = new VcAgentAgent();
        vcAgentAgent.setName(agentParam.getName());
        save(vcAgentAgent);

        VcAgentWx defaultVcAgentWx = agentParam.getDefaultVcAgentWx();
        //添加代理商微信相关配置信息
        VcAgentWx vcAgentWx = new VcAgentWx();
        BeanUtils.copyProperties(defaultVcAgentWx,vcAgentWx);
        vcAgentWx.setId(null);
        vcAgentWx.setAgentId(vcAgentAgent.getId());
        vcAgentWxService.save(vcAgentWx);

//        //添加超管账号信息
//        VcAdminAdmin vcAdminAdmin = new VcAdminAdmin();
//        vcAdminAdmin.setAgentId(vcAgentAgent.getId());
//        vcAdminAdmin.setShopId(0);
//        vcAdminAdmin.setName(agentParam.getAdminName());
//        vcAdminAdmin.setMobile(agentParam.getAdminMobile());
//        vcAdminAdmin.setPassword( MD5Util.encrypt(StringUtils.isEmpty(agentParam.getAdminPassword()) ?
//                agentParam.getAdminMobile().substring(5) : agentParam.getAdminPassword() ,"MD5"));
//        vcAdminAdmin.setStatus(1);
//        vcAdminAdminService.save(vcAdminAdmin);
//
//        VcAdminRole vcAdminRole = vcAdminRoleService.lambdaQuery().eq(VcAdminRole::getIsDelete,0)
//                .eq(VcAdminRole::getType,2).one();
//        //为管理员添加角色信息
//        VcAdminUserRole vcAdminUserRole = new VcAdminUserRole();
//        vcAdminUserRole.setAdminId(vcAdminAdmin.getId());
//        vcAdminUserRole.setRoleId(vcAdminRole.getId());
//        vcAdminUserRoleService.save(vcAdminUserRole);
        return CommonResult.success();
    }


    /**
     * 编辑代理商信息
     * @param agentParam
     * @return
     */
    @Override
    @Transactional
    public CommonResult edit(AgentParam agentParam) {
        //编辑代理商信息
        lambdaUpdate().eq(VcAgentAgent::getId,agentParam.getId()).set(VcAgentAgent::getName,agentParam.getName()).update();

        //修改超管信息
//        VcAdminAdmin vcAdminAdmin = new VcAdminAdmin();
//        vcAdminAdmin.setId(agentParam.getAdminId());
//        vcAdminAdmin.setName(agentParam.getAdminName());
//        vcAdminAdmin.setMobile(agentParam.getAdminMobile());
//        if(!StringUtils.isEmpty(agentParam.getAdminPassword())) {
//            vcAdminAdmin.setPassword(MD5Util.encrypt(agentParam.getAdminPassword() ,"MD5"));
//        }
//        vcAdminAdminService.updateById(vcAdminAdmin);
        return CommonResult.success();
    }


    /**
     * 删除代理商信息
     * @param agentParam
     * @return
     */
    @Override
    @Transactional
    public CommonResult del(AgentParam agentParam) {
        LocalDateTime now = LocalDateTime.now();
        //删除代理商
        lambdaUpdate().eq(VcAgentAgent::getIsDelete,0).eq(VcAgentAgent::getId,agentParam.getId())
                .set(VcAgentAgent::getIsDelete,1).set(VcAgentAgent::getDeleteTime,now).update();

        //删除代理商微信配置信息
        vcAgentWxService.lambdaUpdate().eq(VcAgentWx::getIsDelete,0).eq(VcAgentWx::getAgentId,agentParam.getId())
                .set(VcAgentWx::getIsDelete,1).set(VcAgentWx::getDeleteTime,now).update();

        //删除超管信息
//        vcAdminAdminService.lambdaUpdate().eq(VcAdminAdmin::getIsDelete,0).eq(VcAdminAdmin::getAgentId,agentParam.getId())
//                .set(VcAdminAdmin::getIsDelete,1).set(VcAdminAdmin::getDeleteTime,now).update();
//
//        //删除角色信息
//        vcAdminRoleService.lambdaUpdate().eq(VcAdminRole::getIsDelete,0).eq(VcAdminRole::getAgentId,agentParam.getId())
//                .set(VcAdminRole::getIsDelete,1).set(VcAdminRole::getDeleteTime,now).update();
        return CommonResult.success();
    }
}
