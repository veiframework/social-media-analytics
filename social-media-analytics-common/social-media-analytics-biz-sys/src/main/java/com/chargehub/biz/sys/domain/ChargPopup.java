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
 * 弹框管理
 */
@Data
@TableName("charg_popup")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="charg_popup对象", description="弹框管理")
public class ChargPopup implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id;
	/**标题*/
    @ApiModelProperty(value = "标题")
    private String title;
	/**备注*/
    @ApiModelProperty(value = "备注")
    private String remarks;
	// /**弹窗是否显示(0启用,1关闭)*/
    @ApiModelProperty(value = "弹窗是否显示(Y是,N否)")
    private String type;
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


    @ApiModelProperty(value = "封面照片路径")
    private String url;

    @ApiModelProperty(value = "跳转路径")
    private String jumpPath;
}
