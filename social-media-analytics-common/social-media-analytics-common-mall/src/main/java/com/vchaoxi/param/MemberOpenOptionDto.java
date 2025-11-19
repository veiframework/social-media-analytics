package com.vchaoxi.param;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.vchaoxi.entity.MemberOpenOption;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 15:53
 */
@Data
public class MemberOpenOptionDto implements Z9CrudDto<MemberOpenOption> {

    private Integer rowNum;

    private String errorMsg;



    private Integer id;


    @NotNull
    @ApiModelProperty("会员开通选项类型 0.连续包月  1.单次月卡  2.单次季卡  3.单次年卡")
    private Integer type;

    @NotBlank
    @ApiModelProperty("会员开通选项类型名称")
    private String typeName;

    @NotNull
    @ApiModelProperty("月数 -1表示连续无限制")
    private Integer months;


    @NotNull
    @ApiModelProperty("天数")
    private Integer days;


    @NotNull
    @ApiModelProperty("原价")
    private BigDecimal originalPrice;


    @NotNull
    @ApiModelProperty("售价")
    private BigDecimal price;


    @NotNull
    @ApiModelProperty("选项状态  1上架   0下架")
    private Integer status;





}
