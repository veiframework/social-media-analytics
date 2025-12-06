package com.chargehub.admin.work.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.annotation.CrudSubUniqueId;
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
    private String description;

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

    @CrudSubUniqueId
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

    @ApiModelProperty("社交账号类型 个人- individual, 素人- amateur")
    private String accountType;

    @ApiModelProperty("业务类型")
    private String customType;

    @ApiModelProperty("发布时间")
    private Date postTime;

    @ApiModelProperty("分享链接")
    private String shareLink;

    private Date createTime;

    private Date updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField(exist = false)
    private Integer workNum;

    @ApiModelProperty("来源 manual-手动, sync-同步")
    private String source;

    @ApiModelProperty("点赞变化量")
    private Integer thumbNumChange;

    @ApiModelProperty("点赞增长量")
    private Integer thumbNumUp;

    @ApiModelProperty("播放变化量")
    private Integer playNumChange;

    @ApiModelProperty("播放增长量")
    private Integer playNumUp;

    @ApiModelProperty("同步作品状态 0-待同步，1-同步中，2已同步")
    private Integer syncWorkStatus;

    @ApiModelProperty("同步作品日期")
    private Date syncWorkDate;

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
            int upNum = newWork.getThumbNum() - this.getThumbNum();
            if (upNum >= 0) {
                //有可能因为多线程问题读取到其他线程更改过的数据出现负数所以，出现负数跳过
                updateWork.setThumbNumUp(upNum);
                updateWork.setThumbNumChange(upNum - this.getThumbNumUp());
            }
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
            int upNum = newWork.getPlayNum() - this.getPlayNum();
            if (upNum >= 0) {
                updateWork.setPlayNumUp(upNum);
                updateWork.setPlayNumChange(upNum - this.getPlayNumUp());
            }
        }
        if (!this.getCustomType().equals(newWork.getCustomType())) {
            updateWork.setCustomType(newWork.getCustomType());
        }
        if (!this.getWorkUid().equals(newWork.getWorkUid())) {
            updateWork.setWorkUid(newWork.getWorkUid());
        }
        if (!this.getUrl().equals(newWork.getUrl())) {
            updateWork.setUrl(newWork.getUrl());
        }
        updateWork.setStatisticMd5(newWork.getStatisticMd5());
        return updateWork;
    }

}
