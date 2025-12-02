package com.chargehub.admin.work.dto;

import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaWorkShareLinkDto implements Serializable {

    private static final long serialVersionUID = 1693239330884089812L;


    @NotBlank
    @ApiModelProperty("分享链接")
    private String shareLink;

    private String userId;

    private SocialMediaPlatformEnum platformEnum;
}
