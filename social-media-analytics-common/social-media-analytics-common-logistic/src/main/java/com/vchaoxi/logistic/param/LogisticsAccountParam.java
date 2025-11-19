package com.vchaoxi.logistic.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-30
 */
@Data
public class LogisticsAccountParam {


    @NotNull(groups = {Unbind.class})
    private Integer id;

    /**
     * 商家id
     */
    private Integer shopId;

    /**
     * 快递公司编号
     */
    @NotEmpty(groups = {Bind.class})
    private String deliveryId;

    /**
     * 快递公司名称
     */
    @NotEmpty(groups = {Bind.class})
    private String deliveryName;

    /**
     * 快递公司客户编码
     */
    @NotEmpty(groups = {Bind.class})
    private String bizId;

    /**
     * 快递公司客户密码
     */
    private String password;

    /**
     * 备注内容（提交EMS审核需要） 格式要求： 电话：xxxxx 联系人：xxxxx 服务类型：xxxxx 发货地址：xxxx
     */
    private String remarkContent;


    public interface Bind{}
    public interface Unbind{}

}
