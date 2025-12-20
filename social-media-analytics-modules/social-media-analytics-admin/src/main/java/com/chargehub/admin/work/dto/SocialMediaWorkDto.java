package com.chargehub.admin.work.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.chargehub.admin.work.domain.SocialMediaWork;
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
public class SocialMediaWorkDto implements Serializable, Z9CrudDto<SocialMediaWork> {

    private static final long serialVersionUID = -213837710782592899L;
    private Integer rowNum;

    private String errorMsg;

    private String id;

    @Excel(name = "标题", width = 15)
    @ApiModelProperty("标题")
    private String title;

    @Excel(name = "描述", width = 15)
    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("租户id")
    private String tenantId;

    @Excel(name = "员工", width = 15, dict = "sys_user,nick_name,user_id", addressList = true)
    @ApiModelProperty("用户id")
    private String userId;


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

    @Excel(name = "作品状态", width = 15, dict = "work_state", addressList = true)
    @ApiModelProperty("作品状态")
    private String state;

    @Excel(name = "点赞数量", width = 15)
    @ApiModelProperty("点赞数量")
    private Integer thumbNum;

    @Excel(name = "收藏数量", width = 15)
    @ApiModelProperty("收藏数量")
    private Integer collectNum;

    @Excel(name = "评论量", width = 15)
    @ApiModelProperty("评论量")
    private Integer commentNum;

    @Excel(name = "播放量", width = 15)
    @ApiModelProperty("播放量")
    private Integer playNum;

    @Excel(name = "分享量", width = 15)
    @ApiModelProperty("分享量")
    private Integer shareNum;

    @Excel(name = "喜欢量", width = 15)
    @ApiModelProperty("喜欢量")
    private Integer likeNum;

    @Excel(name = "发布时间", width = 15)
    @ApiModelProperty("发布时间")
    private Date postTime;

    @Dict(dictType = "media_type")
    @Excel(name = "媒体类型", width = 15, dict = "media_type", addressList = true)
    @ApiModelProperty("媒体类型")
    private String mediaType;

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

    @ApiModelProperty("数据统计的md5")
    private String statisticMd5;

    @ApiModelProperty("分享链接")
    private String shareLink;

    private Date createTime;

    private Date updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("来源 manual-手动, sync-同步")
    private String source = "manual";
}
