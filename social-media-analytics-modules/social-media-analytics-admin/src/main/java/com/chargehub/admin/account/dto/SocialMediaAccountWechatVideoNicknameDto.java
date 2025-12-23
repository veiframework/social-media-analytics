package com.chargehub.admin.account.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaAccountWechatVideoNicknameDto implements Serializable {
    private static final long serialVersionUID = -4684369079241122292L;

    @NotBlank
    @ApiModelProperty("社交账号类型 个人- individual, 素人- amateur")
    private String type;

    @NotBlank
    @ApiModelProperty("视频号名称")
    private String nickname;

    private String userId;

    private String tenantId;

}
