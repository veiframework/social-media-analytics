package com.chargehub.admin.work.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaWorkVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = 6590351468561494377L;


    private String id;

    @ApiModelProperty("租户id")
    private String tenantId;

    @Excel(name = "员工", width = 15, dict = "sys_user,nick_name,user_id", addressList = true)
    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    @ApiModelProperty("员工id")
    private String userId;

    @Excel(name = "标题", width = 15)
    @ApiModelProperty("标题")
    private String title;

    @Excel(name = "描述", width = 15)
    @ApiModelProperty("描述")
    private String description;

    @NotBlank
    @Excel(name = "平台", width = 15, dict = "social_media_platform", addressList = true)
    @Dict(dictType = "social_media_platform")
    @ApiModelProperty("平台id")
    private String platformId;

    @NotBlank
    @Excel(name = "第三方账号", width = 15, dict = "social_media_account,nickname,id", addressList = true)
    @Dict(dictTable = "social_media_account", nameColumn = "nickname", valueColumn = "id")
    @ApiModelProperty("第三方账号id")
    private String accountId;


    @Excel(name = "作品链接", width = 15)
    @ApiModelProperty("作品链接")
    private String url;

    @ApiModelProperty("第三方作品id")
    private String workUid;

    @Dict(dictType = "work_type")
    @Excel(name = "作品类型", width = 15, dict = "work_type", addressList = true)
    @ApiModelProperty("作品类型")
    private String type;

    @ApiModelProperty("作品状态")
    private String status;

    @ApiModelProperty("作品数量")
    private Integer workNum;

    @Excel(name = "点赞数量", width = 15)
    @ApiModelProperty("点赞数量")
    private Integer thumbNum;

    @Excel(name = "收藏数量", width = 15)
    @ApiModelProperty("收藏数量")
    private Integer collectNum;

    @Excel(name = "评论数", width = 15)
    @ApiModelProperty("评论量")
    private Integer commentNum;

    @Excel(name = "播放数", width = 15)
    @ApiModelProperty("播放量")
    private Integer playNum;

    @Excel(name = "分享数", width = 15)
    @ApiModelProperty("分享量")
    private Integer shareNum;

    @ApiModelProperty("喜欢量")
    private Integer likeNum;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发布时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("发布时间")
    private Date postTime;

    @Dict(dictType = "media_type")
    @Excel(name = "媒体类型", width = 15, dict = "media_type", addressList = true)
    @ApiModelProperty("媒体类型")
    private String mediaType;

    @ApiModelProperty("数据统计的md5")
    private String statisticMd5;


    @Excel(name = "社交账号类型", width = 15, dict = "social_media_account_type", addressList = true)
    @Dict(dictType = "social_media_account_type")
    @ApiModelProperty("社交账号类型 个人- individual, 素人- amateur")
    private String accountType;

    @Excel(name = "业务类型", width = 15, dict = "social_media_custom_type", addressList = true)
    @Dict(dictType = "social_media_custom_type")
    @ApiModelProperty("业务类型")
    private String customType;

    @ApiModelProperty("话题")
    private String topics;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    private String updater;


    @ApiModelProperty("点赞变化量")
    private Integer thumbNumChange;

    @Excel(name = "点赞增长量",width = 15)
    @ApiModelProperty("点赞增长量")
    private Integer thumbNumUp;

    @ApiModelProperty("播放变化量")
    private Integer playNumChange;

    @Excel(name = "播放增长量",width = 15)
    @ApiModelProperty("播放增长量")
    private Integer playNumUp;

    @ApiModelProperty("来源 manual-手动, sync-同步")
    private String source;

    @Excel(name = "分享链接",width = 15)
    @ApiModelProperty("分享链接")
    private String shareLink;

    @ApiModelProperty("同步作品状态 0-待同步，1-同步中，2已同步")
    private Integer syncWorkStatus;

    @ApiModelProperty("同步作品日期")
    private Date syncWorkDate;
}
