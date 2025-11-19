package com.chargehub.admin.api.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户VO
 *
 * @author system
 * @since 2024-03-21
 */
@Data
@ApiModel(value = "RemoteUserVo", description = "APP用户VO")
public class RemoteUserVo {

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
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

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;


    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;


    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("积分")
    private Integer points;
}