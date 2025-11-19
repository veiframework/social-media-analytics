package com.chargehub.biz.appuser.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.chargehub.biz.appuser.domain.AppUser;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 用户DTO
 * 
 * @author system
 * @since 2024-03-21
 */
@Data
@ApiModel(value = "AppUserDto", description = "APP用户DTO")
public class AppUserDto implements Z9CrudDto<AppUser> {

    /**
     * 行号
     */
    private Integer rowNum;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true)
    @Excel(name = "用户名", width = 15)
    private String username;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    @Excel(name = "昵称", width = 15)
    private String nickname;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Excel(name = "手机号", width = 15)
    private String phone;

    /**
     * 头像URL
     */
    @ApiModelProperty(value = "头像URL")
    @Excel(name = "头像", width = 20)
    private String avatar;

    /**
     * 微信openid
     */
    @ApiModelProperty(value = "微信openid")
    private String openid;

    /**
     * 微信unionid
     */
    @ApiModelProperty(value = "微信unionid")
    private String unionid;

    @NotNull
    @ApiModelProperty("积分")
    private Integer points;
}