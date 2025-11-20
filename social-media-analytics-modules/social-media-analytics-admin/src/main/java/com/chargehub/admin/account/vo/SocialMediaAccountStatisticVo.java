package com.chargehub.admin.account.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.chargehub.common.security.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaAccountStatisticVo implements Serializable {


    private static final long serialVersionUID = -6853444635957771630L;

    private String id;

    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    @ApiModelProperty("用户id")
    private String userId;


    @Excel(name = "第三方用户昵称", width = 15)
    @ApiModelProperty("第三方用户昵称")
    private String nickname;

    @ApiModelProperty("第三方用户id")
    private String uid;

    @ApiModelProperty("租户id")
    private String tenantId;

    @Excel(name = "平台", width = 15, dict = "social_media_platform", addressList = true)
    @Dict(dictType = "social_media_platform")
    @ApiModelProperty("平台id")
    private String platformId;


    @ApiModelProperty("点赞数量")
    private int thumbNum;

    @ApiModelProperty("收藏数量")
    private int collectNum;

    @ApiModelProperty("评论量")
    private int commentNum;

    @ApiModelProperty("播放量")
    private int playNum;

    @ApiModelProperty("分享量")
    private int shareNum;

    @ApiModelProperty("喜欢量")
    private int likeNum;

}
