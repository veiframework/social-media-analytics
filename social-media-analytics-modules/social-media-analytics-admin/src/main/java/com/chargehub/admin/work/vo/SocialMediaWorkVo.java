package com.chargehub.admin.work.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.vo.Z9CrudVo;
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
public class SocialMediaWorkVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = 6590351468561494377L;


    private String id;

    @ApiModelProperty("租户id")
    private String tenantId;

    @Excel(name = "员工", width = 15, dict = "sys_user,nick_name,user_id", addressList = true)
    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    @ApiModelProperty("员工id")
    private String userId;


    @NotBlank
    @Excel(name = "平台", width = 15, dict = "social_media_platform", addressList = true)
    @Dict(dictType = "social_media_platform")
    @ApiModelProperty("平台id")
    private String platformId;

    @NotBlank
    @Excel(name = "第三方账号", width = 15, dict = "social_media_account,nickname,id", addressList = true)
    @Dict(dictType = "social_media_platform")
    @ApiModelProperty("第三方账号id")
    private String accountId;


    @Excel(name = "作品链接", width = 15)
    @ApiModelProperty("作品链接")
    private String url;

    @ApiModelProperty("第三方作品id")
    private String workUid;

    @Excel(name = "平台", width = 15, dict = "work_type", addressList = true)
    @ApiModelProperty("作品类型")
    private String type;

    @ApiModelProperty("作品状态")
    private String status;

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

    private Date createTime;

    private Date updateTime;

    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    private String updater;

}
