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


    @ApiModelProperty("描述")
    private String desc;

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

    @ApiModelProperty("媒体类型")
    private String mediaType;

    @ApiModelProperty("数据统计的md5")
    private String statisticMd5;

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

    public String generateStatisticMd5() {
        return String.join("_",
                this.thumbNum.toString(),
                this.shareNum.toString(),
                this.likeNum.toString(),
                this.collectNum.toString(),
                this.commentNum.toString(),
                this.playNum.toString());
    }

    public SocialMediaWork computeMd5(SocialMediaWork newWork) {
        if (this.getStatisticMd5().equals(newWork.getStatisticMd5())) {
            return null;
        }
        SocialMediaWork updateWork = new SocialMediaWork();
        updateWork.setId(this.getId());
        if (!this.getThumbNum().equals(newWork.getThumbNum())) {
            updateWork.setThumbNum(newWork.getThumbNum());
        }
        if (!this.getShareNum().equals(newWork.getShareNum())) {
            updateWork.setShareNum(newWork.getShareNum());
        }
        if (!this.getLikeNum().equals(newWork.getLikeNum())) {
            updateWork.setLikeNum(newWork.getLikeNum());
        }
        if (!this.getCollectNum().equals(newWork.getCollectNum())) {
            updateWork.setCollectNum(newWork.getCollectNum());
        }
        if (!this.getCommentNum().equals(newWork.getCommentNum())) {
            updateWork.setCommentNum(newWork.getCommentNum());
        }
        if (!this.getPlayNum().equals(newWork.getPlayNum())) {
            updateWork.setPlayNum(newWork.getPlayNum());
        }
        return updateWork;
    }

}
