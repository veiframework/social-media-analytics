package com.chargehub.admin.work.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaWork implements Serializable, Z9CrudEntity {
    private static final long serialVersionUID = 6710016428713985450L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("租户id")
    private String tenantId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("平台id")
    private String platformId;

    @ApiModelProperty("第三方账号id")
    private String accountId;

    @ApiModelProperty("作品链接")
    private String url;

    @ApiModelProperty("第三方作品id")
    private String workUid;

    @ApiModelProperty("作品类型")
    private String type;

    @ApiModelProperty("作品状态")
    private String status;

    @ApiModelProperty("点赞数量")
    private Integer thumbNum;

    @ApiModelProperty("收藏数量")
    private Integer collectNum;

    @ApiModelProperty("评论量")
    private Integer commentNum;

    @ApiModelProperty("播放量")
    private Integer playNum;

    @ApiModelProperty("分享量")
    private Integer shareNum;

    @ApiModelProperty("喜欢量")
    private Integer likeNum;

    @ApiModelProperty("发布时间")
    private Date postTime;

    private Date createTime;

    private Date updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;


    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}
