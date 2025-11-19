package com.chargehub.common.security.template.domain;

import com.chargehub.common.security.annotation.Logical;
import com.chargehub.common.security.auth.AuthUtil;
import lombok.Data;

/**
 * @author Zhanghaowei
 * @date 2024/05/27 12:49
 */
@Data
public class Z9CrudPermission {

    /**
     * 需要校验的角色标识
     */
    private String[] value;

    /**
     * 验证逻辑：AND | OR，默认AND
     */
    private Logical logical = Logical.AND;


    public Z9CrudPermission(String[] value) {
        this.value = value;
    }

    public Z9CrudPermission(String[] value, Logical logical) {
        this.value = value;
        this.logical = logical;
    }

    public void doCheck() {
        if (getLogical() == Logical.AND) {
            AuthUtil.checkPermiAnd(this.value);
        } else {
            AuthUtil.checkPermiOr(this.value);
        }
    }

    public static Z9CrudPermission nullPermission() {
        return new Z9CrudPermission(null) {
            @Override
            public void doCheck() {
                //nothing to do
            }
        };
    }

}
