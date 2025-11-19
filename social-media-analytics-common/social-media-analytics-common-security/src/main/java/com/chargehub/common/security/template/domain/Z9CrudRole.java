package com.chargehub.common.security.template.domain;

import com.chargehub.common.security.annotation.Logical;
import com.chargehub.common.security.auth.AuthUtil;
import lombok.Data;

/**
 * @author Zhanghaowei
 * @date 2024/05/27 12:49
 */
@Data
public class Z9CrudRole {

    /**
     * 需要校验的角色标识
     */
    private String[] value;

    /**
     * 验证逻辑：AND | OR，默认AND
     */
    private Logical logical = Logical.AND;

    public Z9CrudRole(String[] value) {
        this.value = value;
    }

    public Z9CrudRole(String[] value, Logical logical) {
        this.value = value;
        this.logical = logical;
    }

    public void doCheck() {
        if (getLogical() == Logical.AND) {
            AuthUtil.checkRoleAnd(this.value);
        } else {
            AuthUtil.checkRoleOr(this.value);
        }
    }

    public static Z9CrudRole nullRole() {
        return new Z9CrudRole(null) {
            @Override
            public void doCheck() {
                //nothing to do
            }
        };
    }

}
