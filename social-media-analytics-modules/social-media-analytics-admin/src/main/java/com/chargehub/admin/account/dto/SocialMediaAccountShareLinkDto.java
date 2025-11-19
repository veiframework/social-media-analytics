package com.chargehub.admin.account.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaAccountShareLinkDto implements Serializable {
    private static final long serialVersionUID = -7664321231443401267L;

    @NotBlank
    @ApiModelProperty("分享链接")
    private String shareLink;

    private String userId;

}
