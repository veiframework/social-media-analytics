package com.vchaoxi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.chargehub.biz.appuser.dto.AppUserInfoDto;
import com.chargehub.biz.appuser.service.UserInfoExtension;
import com.chargehub.biz.appuser.vo.AppUserVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vchaoxi.constant.OrderConstant;
import com.vchaoxi.constant.SysConstant;
import com.vchaoxi.entity.VcOrderOrder;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.mapper.VcUserUserMapper;
import com.vchaoxi.service.IVcOrderOrderService;
import com.vchaoxi.service.IVcUserUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zhanghaowei
 * @date 2025/08/04 09:31
 */
@Service
public class UserInfoExtensionImpl implements UserInfoExtension {


    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private IVcUserUserService vcUserUserService;

    @Autowired
    private IVcOrderOrderService vcOrderOrderService;

    @Override
    public ObjectNode getExtendParams(String userId, Object loginUser) {
        AppUserVo userVo = (AppUserVo) loginUser;
        ObjectNode objectNode = objectMapper.createObjectNode();
        //同步mall的user
        VcUserUser vcUserUser = vcUserUserService.lambdaQuery().eq(VcUserUser::getLoginId, userId).one();
        if (vcUserUser == null) {
            vcUserUser = new VcUserUser();
            vcUserUser.setLoginId(userId);
            vcUserUser.setMaOpenid(userVo.getOpenid());
            vcUserUser.setUnionid(userVo.getUnionid());
            vcUserUser.setNickname(userVo.getNickname());
            vcUserUser.setHeadimgUrl(userVo.getAvatar());
            vcUserUserService.save(vcUserUser);
        }
        Long notPayNum = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getUserId, vcUserUser.getId())
                .eq(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_NOT_PAY).count();
        Long paidNum = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getUserId, vcUserUser.getId())
                .eq(VcOrderOrder::getStatus, OrderConstant.ORDER_PAY_STATUS_PAY).count();
        Long shippedNum = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getUserId, vcUserUser.getId())
                .eq(VcOrderOrder::getStatus, OrderConstant.ORDER_PAY_STATUS_SHIPPED).count();
        Long refundNum = vcOrderOrderService.lambdaQuery().eq(VcOrderOrder::getUserId, vcUserUser.getId())
                .eq(VcOrderOrder::getStatus, OrderConstant.ORDER_STATUS_REQUIRE_REFUND).count();
        objectNode.put("notPayNum", notPayNum);
        objectNode.put("paidNum", paidNum);
        objectNode.put("refundNum", refundNum);
        objectNode.put("shippedNum", shippedNum);
        objectNode.put("memGrade", vcUserUser.getMemGrade());
        return objectNode;
    }

    @Override
    public void updateUserInfo(AppUserInfoDto dto) {
        vcUserUserService.lambdaUpdate()
                .eq(VcUserUser::getLoginId, dto.getId())
                .set(StringUtils.isNotBlank(dto.getNickname()), VcUserUser::getNickname, dto.getNickname())
                .set(StringUtils.isNotBlank(dto.getPhone()), VcUserUser::getPhone, dto.getPhone())
                .set(StringUtils.isNotBlank(dto.getAvatar()), VcUserUser::getHeadimgUrl, dto.getAvatar())
                .update();
    }


}
