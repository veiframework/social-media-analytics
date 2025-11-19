package com.chargehub.biz.sys.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/23 14:36
 * @Project：chargehub
 * @Package：com.chargehub.biz.sys.domain
 * @Filename：ChargeSysConfig
 */
@Data
@TableName("charge_sys_config")
public class ChargeSysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * name
     */
    private String configName;

    /**
     * key
     */
    private String configKey;


    /**
     * value
     */
    private String configValue;

    /**
     * configType 系统内置（Y是 N否）
     */
    private String configType;

    /**
     * 备注
     */
    private String remark;


}

