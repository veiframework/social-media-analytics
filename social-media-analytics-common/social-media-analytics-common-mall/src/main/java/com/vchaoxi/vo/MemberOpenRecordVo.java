package com.vchaoxi.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 17:25
 */
@Data
public class MemberOpenRecordVo implements Z9CrudVo {


    private String id;

    @ApiModelProperty("用户登录id")
    private String userId;

    @ApiModelProperty("区域id")
    private String regionId;

    @ApiModelProperty("会员开通选项类型 0.连续包月  1.单次月卡  2.单次季卡  3.单次年卡")
    private Integer type;


    @ApiModelProperty("会员开通选项类型名称")
    private String typeName;


    @ApiModelProperty("月数 -1表示连续无限制")
    private Integer months;


    @ApiModelProperty("天数")
    private Integer days;


    @ApiModelProperty("原价")
    private BigDecimal originalPrice;


    @ApiModelProperty("售价")
    private BigDecimal price;


    @ApiModelProperty("开通时间(支付时间)")
    private Date openTime;

    @ApiModelProperty("开通记录状态  0待支付  1已支付 2退款中  3已退款")
    private Integer status;

    @ApiModelProperty("开始时间")
    private Date startDate;

    @ApiModelProperty("结束时间")
    private Date endDate;

    @ApiModelProperty("会员来源方式")
    private Integer source;

    @ApiModelProperty("会员来源记录id")
    private String sourceRecordId;

    @ApiModelProperty("是否正在生效 0不生效, 1生效中")
    private Integer active;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("退款时间")
    private Date refundTime;

    @ApiModelProperty("退款备注")
    private String refundRemarks;

    @ApiModelProperty("退款编号")
    private String refundNo;

    @ApiModelProperty("退款金额")
    private BigDecimal refundMoney;


    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @ApiModelProperty("修改时间")
    private Date updateTime;



}
