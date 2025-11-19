package com.vchaoxi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vchaoxi.entity.VcUserUser;

/**
 * <p>
 * 用户相关 - 用户表 服务类
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface IVcUserUserService extends IService<VcUserUser> {

    default VcUserUser getCurrentUserNoException() {
        return getCurrentUserBy(false);
    }

    default VcUserUser getCurrentUser() {
        return getCurrentUserBy(true);
    }

    VcUserUser getCurrentUserBy(boolean throwException);

    /**
     * 通过用户id获取用户账号
     *
     * @param userId
     * @return
     */
    public Integer getAccountById(Integer userId);

    /**
     * 更新邀请人登录id
     *
     * @param currentUserId
     * @param inviteLoginId
     */
    void updateInviteLoginId(Integer currentUserId, String inviteLoginId);
}
