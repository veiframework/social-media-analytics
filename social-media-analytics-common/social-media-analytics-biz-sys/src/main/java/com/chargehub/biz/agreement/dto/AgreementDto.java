package com.chargehub.biz.agreement.dto;


import com.chargehub.biz.agreement.domain.Agreement;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Zhanghaowei
 * @date 2025/08/08 16:40
 */
@Data
public class AgreementDto implements Z9CrudDto<Agreement> {

    private Integer rowNum;
    private String errorMsg;

    @ApiModelProperty(value = "协议类型")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;


}
