package com.chargehub.biz.sys.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
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

/**
 * @Description: 留言管理
 * @Author: jeecg-boot
 * @Date:   2023-07-31
 * @Version: V1.0
 */
@Data
@TableName("charg_message")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="charg_message对象", description="留言管理")
public class ChargMessage implements Serializable {
    private static final long serialVersionUID = 1L;

	/**注释*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "注释")
    private Integer id;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private String content;
	/**星星评分*/
	@Excel(name = "星星评分", width = 15)
    @ApiModelProperty(value = "星星评分")
    private Integer starRating;
	/**联系方式*/
	@Excel(name = "联系方式", width = 15)
    @ApiModelProperty(value = "联系方式")
    private String phone;
	@Excel(name = "添加人", width = 15)
	@ApiModelProperty(value = "添加人")
	@Dict(dictTable = "`charg_user`",valueColumn = "id",nameColumn = "phone")
	private String userId;

	@Excel(name = "添加人昵称", width = 15)
	@Dict(dictTable = "`charg_user`",valueColumn = "id",nameColumn = "nickname")
	@TableField(exist = false)
	private String nick;

	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "添加时间")
	@Excel(name = "添加时间", width = 15,format = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**状态(0未解决,1已解决)*/
	@Excel(name = "状态", width = 15, replace = {"未解决_0,开始处理_1,已解决_2"})
	@Dict(dictTable = "message_type")
    @ApiModelProperty(value = "状态(未解决_0,开始处理_1,已解决_2)")
    private String type;

	@Excel(name = "图片", width = 15,type = 2)
	@ApiModelProperty(value = "图片地址")
	private String imgUrl;
	/**回复留言id*/
    @ApiModelProperty(value = "回复留言id")
    private Integer replyId;
	/**回复帖子id*/
    @ApiModelProperty(value = "回复帖子id")
    private Integer postId;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "回复人id")
    private String recoverUserId;
}
