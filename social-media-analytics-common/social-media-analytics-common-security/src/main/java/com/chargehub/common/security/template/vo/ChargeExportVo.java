package com.chargehub.common.security.template.vo;

import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import com.chargehub.common.security.annotation.Dict;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2024/07/16 17:21
 */
@Data
public class ChargeExportVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = 7332190681275755364L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "业务编码")
    private String businessCode;

    @ApiModelProperty(value = "相对路径")
    private String uri;

    @ApiModelProperty(value = "请求参数")
    private ObjectNode requestParams;

    @ApiModelProperty(value = "导出状态0-导出中,1导出成功,2导出失败")
    private Integer exportStatus;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "失败原因")
    private String failedReason;

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

    @Dict(dictTable = "sys_user", nameColumn = "user_name", valueColumn = "user_id")
    @ExcelIgnore
    @Schema(description = "创建者")
    private String createBy;

    @ExcelIgnore
    @Schema(description = "创建时间")
    private Date createTime;

    @Dict(dictTable = "sys_user", nameColumn = "user_name", valueColumn = "user_id")
    @ExcelIgnore
    @Schema(description = "更新者")
    private String updateBy;

    @ExcelIgnore
    @Schema(description = "修改时间")
    private Date updateTime;




}
