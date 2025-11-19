package com.chargehub.admin.groupuser.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class GroupUserBatchAddDto implements Serializable {
    private static final long serialVersionUID = 4618186087743644765L;

    @NotEmpty
    @ApiModelProperty("用户id")
    private Set<String> userIds;

    @ApiModelProperty("父用户id")
    private String parentUserId;

}
