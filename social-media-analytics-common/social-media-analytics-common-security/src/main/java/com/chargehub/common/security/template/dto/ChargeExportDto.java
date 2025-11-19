package com.chargehub.common.security.template.dto;

import com.chargehub.common.security.template.domain.ChargeExport;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/07/16 17:21
 */
@Data
public class ChargeExportDto implements Z9CrudDto<ChargeExport>, Serializable {

    private static final long serialVersionUID = 5506815332543192226L;

    @NotBlank
    @ApiModelProperty(value = "业务编码")
    private String businessCode;

    @ApiModelProperty(value = "相对路径")
    private String uri;

    @ApiModelProperty(value = "请求参数")
    private ObjectNode requestParams;

    @ApiModelProperty(value = "备注")
    private String remark;

    private Integer rowNum;


    private String errorMsg;

}
