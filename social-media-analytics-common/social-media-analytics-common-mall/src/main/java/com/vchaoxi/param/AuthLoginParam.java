package com.vchaoxi.param;


import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class AuthLoginParam {

    @NotEmpty(groups = {MaAuth.class,PureMaAuth.class})
    private String maCode;
    @NotEmpty(groups = {MaAuth.class,PureMaAuth.class})
    private String maAppId;
    /**
     * 邀请者的id
     */
    private Integer inviterId;


    public interface MaAuth {}
    public interface PureMaAuth {}
}
