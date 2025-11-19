package com.chargehub.biz.sys.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/29 18:23
 * @Project：chargehub
 * @Package：com.chargehub.biz.sys.domain
 * @Filename：ChargRefundSet
 */
@TableName("charg_refund_set")
@Data
public class ChargRefundSet implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;

    /**配置名称*/
    @ApiModelProperty(value = "配置名称")
    private String name;

    /**配置信息*/
    @ApiModelProperty(value = "配置信息")
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private Object value;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    /**修改时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "备注")
    private String remarks;
}