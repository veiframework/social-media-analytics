package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserBannerParam {
    /**
     * Banner 名称
     */
    @NotEmpty
    private String name;

    /**
     * Banner url
     */
    @NotEmpty
    private String url;

    /**
     * Banner跳转链接
     */
    private String link;
}
