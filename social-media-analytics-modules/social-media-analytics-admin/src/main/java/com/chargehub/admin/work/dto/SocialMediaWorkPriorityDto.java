package com.chargehub.admin.work.dto;

import com.chargehub.admin.work.domain.SocialMediaWorkPriority;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaWorkPriorityDto implements Serializable, Z9CrudDto<SocialMediaWorkPriority> {
    private static final long serialVersionUID = 2179713595155170307L;

    private Integer rowNum;
    private String errorMsg;

    private String id;

    @ApiModelProperty("优先级")
    private String priority;

    @ApiModelProperty("正常采集间隔")
    private Integer normalInterval;

    @ApiModelProperty("堆积时采集间隔")
    private Integer backlogInterval;

    @ApiModelProperty("表达式")
    private String priorityExpression;


}
