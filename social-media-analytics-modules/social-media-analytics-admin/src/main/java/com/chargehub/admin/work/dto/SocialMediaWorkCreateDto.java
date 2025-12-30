package com.chargehub.admin.work.dto;

import com.chargehub.admin.work.domain.SocialMediaWorkCreate;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaWorkCreateDto implements Z9CrudDto<SocialMediaWorkCreate> {

    private Integer rowNum;
    private String errorMsg;

    private String id;


    @NotBlank
    @ApiModelProperty("分享链接")
    private String shareLink;

    @NotBlank
    @ApiModelProperty("账号类型")
    private String accountType;

    @ApiModelProperty("重试次数")
    private Integer retryCount;
}
