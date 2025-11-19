package com.vchaoxi.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 通用配置信息字段表
 * </p>
 *
 * @author hanfuxian
 * @since 2024-08-05
 */
@Data
public class AgentGeneralConfigParams implements Serializable {

    @NotNull(groups = {Update.class, Del.class}, message = "核销信息ID不能为空")
    private Integer id;

    /**
     * 线下核销地址
     */
    @NotEmpty(groups = {Add.class, Update.class}, message = "核销地址信息ID不能为空")
    private String offlineRedemptionAddress;
    /**
     * 线下核销联系人
     */
    @NotEmpty(groups = {Add.class, Update.class}, message = "核销联系人不能为空")
    private String offlineRedemptionContact;
    /**
     * 线下核销联系方式
     */
    @NotEmpty(groups = {Add.class, Update.class}, message = "核销联系方式不能为空")
    private String offlineRedemptionPhone;

    /**
     * 添加时间
     */
    private LocalDateTime insertTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime updateTime;

    public interface Add {
    }

    public interface Update {
    }

    public interface Del {
    }
}
