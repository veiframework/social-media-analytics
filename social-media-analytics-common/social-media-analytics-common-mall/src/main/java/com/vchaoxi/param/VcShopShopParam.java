package com.vchaoxi.param;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * <p>
 * 商铺相关 - 商铺表
 * </p>
 *
 * @author wangjiangtao
 * @since 2023-06-14
 */
@Data
public class VcShopShopParam {

    @NotNull(groups = {Edit.class,Del.class})
    private Integer id;
    @NotNull(groups = {Add.class})
    private Integer agentId;
    @NotEmpty(groups = {Add.class})
    private String name;
    private String logo;
    private String brand;
    private String contacts;
    @NotEmpty(groups = {Add.class})
    private String servicePhone;

    @NotEmpty(groups = {Add.class})
    private String adminMobile;
    @NotEmpty(groups = {Add.class})
    private String adminName;
    private String adminPassword;



    public interface Add {}
    public interface Edit {}
    public interface Del {}

}
