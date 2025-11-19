package com.vchaoxi.vo;

import lombok.Data;

@Data
public class MaUnlimitCodeVo {
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 小程序id
     */
    private String maAppid;

    private String scene;

    private String page;

    private Integer checkPath;

    /**
     * 环境
     */
    private String envVersion;

    /**
     * url地址
     */
    private String url;

}
