package com.chargehub.admin.account.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.chargehub.admin.account.domain.SocialMediaAccount;
import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaAccountDto implements Serializable, Z9CrudDto<SocialMediaAccount> {
    private static final long serialVersionUID = 4967450013415950609L;

    private Integer rowNum;

    private String errorMsg;


    private String id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("租户id")
    private String tenantId;

    @NotBlank
    @Excel(name = "平台", width = 15, dict = "social_media_platform", addressList = true)
    @Dict(dictType = "social_media_platform")
    @ApiModelProperty("平台id")
    private String platformId;

    @ApiModelProperty("第三方用户id")
    private String uid;

    @NotBlank
    @ApiModelProperty("第三方用户加密id")
    private String secUid;

    @Excel(name = "第三方用户昵称", width = 15)
    @ApiModelProperty("第三方用户昵称")
    private String nickname;

    @ApiModelProperty("accessToken")
    private String accessToken;

    @ApiModelProperty("refreshToken")
    private String refreshToken;

    @ApiModelProperty("过期时间")
    private Date expireTime;

    @ApiModelProperty("第三方openId")
    private String openId;


    @Excel(name = "社交账号类型", width = 15, dict = "social_media_account_type", addressList = true)
    @Dict(dictType = "social_media_account_type")
    @ApiModelProperty("社交账号类型 个人- individual, 素人- amateur")
    private String type;


}
