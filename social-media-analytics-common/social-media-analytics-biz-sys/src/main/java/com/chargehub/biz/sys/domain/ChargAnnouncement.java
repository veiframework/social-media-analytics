package com.chargehub.biz.sys.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 公告类型
 * @Author: jeecg-boot
 * @Date:   2023-07-31
 * @Version: V1.0
 */
@Data
@TableName("charg_announcement")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="charg_announcement", description="公告类型")
public class ChargAnnouncement implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private String id;
	/**公告类型*/
    @ApiModelProperty(value = "公告类型")
    private String announcementType;
	/**状态(0启用，1关闭)*/
    @ApiModelProperty(value = "状态(0启用，1关闭)")
    private Integer type;
	/**是否默认展开(0启用,1关闭)*/
    @ApiModelProperty(value = "是否默认展开(0启用,1关闭)")
    private Integer expansiType;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}
