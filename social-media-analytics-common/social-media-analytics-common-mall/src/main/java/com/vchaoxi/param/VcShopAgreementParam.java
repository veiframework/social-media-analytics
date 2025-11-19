package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 应用说明
 * </p>
 *
 * @author hanfuxian
 * @since 2024-09-19
 */
@Data
public class VcShopAgreementParam {

    private Integer id;

    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 客户ID
     */
    private Integer shopId;

    /**
     * 注册说明
     */
    @NotBlank(message = "注册说明不能为空")
    private String signupDetails;

    /**
     * 升级说明
     */
    @NotBlank(message = "升级说明不能为空")
    private String upgradeDetails;


}
