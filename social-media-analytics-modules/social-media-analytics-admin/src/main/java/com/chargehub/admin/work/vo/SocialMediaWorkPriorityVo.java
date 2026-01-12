package com.chargehub.admin.work.vo;

import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaWorkPriorityVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = 7127657876243620463L;


    private String id;

    @ApiModelProperty("优先级")
    private String priority;

    @ApiModelProperty("正常采集间隔")
    private Integer normalInterval;

    @ApiModelProperty("堆积时采集间隔")
    private Integer backlogInterval;

    @ApiModelProperty("表达式")
    private String priorityExpression;

    private Date createTime;

    private Date updateTime;

    private String creator;

    private String updater;


}
