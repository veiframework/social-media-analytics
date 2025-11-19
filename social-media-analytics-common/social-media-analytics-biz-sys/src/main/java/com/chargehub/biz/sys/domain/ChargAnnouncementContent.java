package com.chargehub.biz.sys.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chargehub.common.security.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("charg_announcement_content")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "charg_announcement_content对象", description = "charg_announcement_content")
public class ChargAnnouncementContent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;
    /**
     * 标题
     */

    @ApiModelProperty(value = "标题")
    private String title;
    /**
     * 类型id
     */


    @ApiModelProperty(value = "公告类型id")
    @Dict(dictTable = "`charg_announcement`",valueColumn = "id",nameColumn = "announcement_type")
    private String announcementId;

    /**
     * 公告类型()
     */
    @ApiModelProperty(value = " 公告类型()")
    @Dict(dictTable = "announcement_type")
    private String contentType;

    /**
     * 封面照片路径
     */
    @ApiModelProperty(value = "封面照片路径")
    private java.lang.String url;

    /**
     * 内容链接
     */
    @ApiModelProperty(value = "内容链接")
    private java.lang.String contentLink;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private java.lang.String content;

    /**
     * 下发状态
     */
    @ApiModelProperty(value = "下发状态")
    @Dict(dictTable = "announcement_delivery_type")
    private String deliveryType;

    /**
     * 是否置顶
     */
    @ApiModelProperty(value = "是否置顶")
    @Dict(dictTable = "announcement_top_type")
    private String izTop;

    /**
     * 文字类型（0：文字；1：视频）
     */
    @ApiModelProperty(value = "文字类型（0：文字；1：视频）")
    @Dict(dictTable = "announcement_content_type")
    private String wordType;

    @ApiModelProperty(value = "创建者")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;


    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private String endTime;

    /**
     * 位置(0洛阳,1郑州)
     */
    @ApiModelProperty(value = "位置(0洛阳,1郑州)")
    private Integer locations;

}
