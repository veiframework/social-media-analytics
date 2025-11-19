package com.chargehub.biz.appuser.service;

import com.chargehub.biz.appuser.dto.AppUserInfoDto;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Zhanghaowei
 * @date 2025/08/04 09:35
 */
public interface UserInfoExtension {

    /**
     * 追加属性
     *
     * @param loginUser
     * @param userId
     * @return
     */
    ObjectNode getExtendParams(String userId, Object loginUser);

    /**
     * 更新用户信息
     *
     * @param dto
     */
    void updateUserInfo(AppUserInfoDto dto);
}
