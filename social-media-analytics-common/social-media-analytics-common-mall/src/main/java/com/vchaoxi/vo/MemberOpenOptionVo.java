package com.vchaoxi.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 15:59
 */
@Data
public class MemberOpenOptionVo implements Z9CrudVo {


    private Integer id;


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


    @ApiModelProperty("选项状态  1上架   0下架")
    private Integer status;


    @Dict(dictTable = "sys_user", nameColumn = "user_name", valueColumn = "user_id")
    @ApiModelProperty("创建人")
    private String createBy;


    @ApiModelProperty("创建时间")
    private Date createTime;


    @Dict(dictTable = "sys_user", nameColumn = "user_name", valueColumn = "user_id")
    @ApiModelProperty("修改人")
    private String updateBy;


    @ApiModelProperty("修改时间")
    private Date updateTime;


}
