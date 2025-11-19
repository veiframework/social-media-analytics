package com.chargehub.biz.sys.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2024/12/04 14:00
 */

@TableName(value = "charge_gun_offline_time", schema = "analytics")
@Data
public class ChargeGunOfflineTime implements Serializable {
    private static final long serialVersionUID = -6020589884715593327L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String gunId;

    private String stationId;

    private String regionId;

    private Long offlineTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
