package com.chargehub.biz.appuser.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户VO
 *
 * @author system
 * @since 2024-03-21
 */
@Data
@ApiModel(value = "AppUserVo", description = "app用户VO")
public class AppUserVo implements Z9CrudVo, Serializable {

    private static final long serialVersionUID = 5688342705931764950L;

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
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
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


    @ApiModelProperty("扩展参数")
    private ObjectNode extendParams;


}