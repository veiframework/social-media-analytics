package com.chargehub.common.security.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/04/02 13:50
 */
@TableName("sys_dict_data")
@Data
public class ChargeSysDictData implements Serializable {


    private static final long serialVersionUID = 317655684992166542L;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典键值
     */
    private String dictValue;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 备注
     */
    private String remark;

}
