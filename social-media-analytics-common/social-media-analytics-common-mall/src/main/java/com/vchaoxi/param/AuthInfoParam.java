package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuthInfoParam {
    /**
     * 用户id
     */
    @NotNull(groups = {Edit.class})
    private Integer id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String headimgUrl;

    /**
     * 手机号
     */
    private String phone;




    public interface Edit {}
}
