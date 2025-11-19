package com.chargehub.common.datascope.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Zhanghaowei
 * @date 2024/04/20 09:09
 */
@Data
@TableName("sys_user_role")
public class SysPurviewUserRole {

    private Long userId;

    private Long roleId;

    public SysPurviewUserRole() {
    }

    public SysPurviewUserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
