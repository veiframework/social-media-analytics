package com.chargehub.admin.account.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaAccountVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = 8095728598708906950L;


    private String id;

    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("租户id")
    private String tenantId;

    @Excel(name = "平台", width = 15, dict = "social_media_platform", addressList = true)
    @Dict(dictType = "social_media_platform")
    @ApiModelProperty("平台id")
    private String platformId;

    @ApiModelProperty("第三方用户id")
    private String uid;

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

    @Excel(name = "同步作品状态", width = 15, dict = "sync_work_status", addressList = true)
    @Dict(dictType = "sync_work_status")
    @ApiModelProperty("同步作品状态 0-待同步，1-同步中，2已同步")
    private String syncWorkStatus;

    @Excel(name = "社交账号类型", width = 15, dict = "social_media_account_type", addressList = true)
    @Dict(dictType = "social_media_account_type")
    @ApiModelProperty("社交账号类型 个人- individual, 素人- amateur")
    private String type;


    private Date createTime;

    private Date updateTime;

    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    private String creator;

    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    private String updater;

    @ApiModelProperty("同步作品日期")
    private Date syncWorkDate;

    @ApiModelProperty("是否开启自动同步")
    private String autoSync;


    @ApiModelProperty("是否是爬虫")
    private Integer crawler;
}
