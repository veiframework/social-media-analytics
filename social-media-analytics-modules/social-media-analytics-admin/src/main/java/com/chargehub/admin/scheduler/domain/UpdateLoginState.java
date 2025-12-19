package com.chargehub.admin.scheduler.domain;

import lombok.Data;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class UpdateLoginState {

    private String loginState;

    public UpdateLoginState(String loginState) {
        this.loginState = loginState;
    }

}
