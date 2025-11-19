package com.vchaoxi.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2025/08/27 18:02
 */
@Data
public class CommissionRecordVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = -1083688189057848646L;

    @TableId(type = IdType.AUTO)
    private String id;

    @ApiModelProperty("登录人id")
    private String loginId;

    @Dict(dictTable = "app_user", nameColumn = "nickname", valueColumn = "id")
    @ApiModelProperty("来源用户的id")
    private String sourceLoginId;

    @ApiModelProperty("类型: commission-提成, withdraw-提现")
    private String type;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("状态- received到账, waiting- 即将到账")
    private String status;

    @ApiModelProperty("订单id")
    private String orderId;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("商品信息")
    private List<String> goodsInfo;

    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @ApiModelProperty("修改时间")
    private Date updateTime;


}
