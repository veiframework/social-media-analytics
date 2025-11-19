package com.chargehub.common.security.template.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2024/07/16 16:56
 */
@Data
@TableName(value = "charge_export", autoResultMap = true)
public class ChargeExport implements Z9CrudEntity, Serializable {

    private static final long serialVersionUID = -7107051596030947953L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "业务编码")
    private String businessCode;

    @ApiModelProperty(value = "相对路径")
    private String uri;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "失败原因")
    private String failedReason;

    @ApiModelProperty(value = "请求参数")
    @TableField(value = "request_params", typeHandler = JacksonTypeHandler.class)
    private ObjectNode requestParams;

    @ApiModelProperty(value = "导出状态0-导出中,1导出成功,2导出失败")
    private Integer exportStatus;

    @ApiModelProperty(value = "花费时间")
    private Double spendTime;


    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "总数量")
    private Integer totalNum;

    @ApiModelProperty(value = "当前数量")
    private Integer currentNum;

    @ApiModelProperty(value = "导出进度")
    private BigDecimal exportProgress;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;


    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    @ApiModelProperty(value = "更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;


    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Boolean delFlag;

    @Override
    public String getUniqueId() {
        return getId();
    }

    @Override
    public void setUniqueId(String id) {
        setId(id);
    }
}
