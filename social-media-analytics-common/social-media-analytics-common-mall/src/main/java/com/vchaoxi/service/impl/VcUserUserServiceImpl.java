package com.vchaoxi.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chargehub.admin.api.model.LoginUser;
import com.chargehub.common.security.utils.SecurityUtils;
import com.vchaoxi.constant.SysConstant;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.mapper.VcUserUserMapper;
import com.vchaoxi.service.IVcUserUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户相关 - 用户表 服务实现类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@Service
public class VcUserUserServiceImpl extends ServiceImpl<VcUserUserMapper, VcUserUser> implements IVcUserUserService {


    @Override
    public VcUserUser getCurrentUserBy(boolean throwException) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Assert.notNull(loginUser, "用户尚未登陆");
            Long userid = loginUser.getUserid();
            VcUserUser one = this.lambdaQuery().eq(VcUserUser::getLoginId, userid + "").one();
            Assert.notNull(loginUser, "用户尚未登陆");
            return one;
        } catch (Exception e) {
            if (throwException) {
                throw e;
            }
            return null;
        }

    }

    /**
     * 获取用户账号
     *
     * @param userId
     * @return
     */
    @Override
    public Integer getAccountById(Integer userId) {
        if (userId == null) {
            return null;
        }

        if (SysConstant.UNBOUND_USERID.equals(userId) || SysConstant.UNKNOWN_USERID.equals(userId)) {
            return userId;
        }

        return userId + SysConstant.USER_ACCOUNT_BASE;
    }

    @Override
    public void updateInviteLoginId(Integer currentUserId, String inviteLoginId) {
        this.lambdaUpdate()
                .set(VcUserUser::getInviteLoginId, inviteLoginId)
                .eq(VcUserUser::getId, currentUserId)
                .isNull(VcUserUser::getInviteLoginId)
                .update();
    }
}
