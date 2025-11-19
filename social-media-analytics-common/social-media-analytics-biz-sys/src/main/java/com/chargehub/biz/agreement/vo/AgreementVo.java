package com.chargehub.biz.agreement.vo;

import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2025/08/08 16:34
 */
@Data
public class AgreementVo implements Z9CrudVo {


    private String id;

    @Dict(dictType = "agreement_type")
    @ApiModelProperty(value = "协议类型")
    private String type;

    @ApiModelProperty(value = "内容")
    private String content;

    @Dict(dictTable = "sys_user", nameColumn = "user_name", valueColumn = "user_id")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Dict(dictTable = "sys_user", nameColumn = "user_name", valueColumn = "user_id")
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
