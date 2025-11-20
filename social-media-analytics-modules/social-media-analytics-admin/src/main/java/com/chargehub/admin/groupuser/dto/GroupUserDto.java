package com.chargehub.admin.groupuser.dto;

import com.chargehub.admin.groupuser.domain.GroupUser;
import com.chargehub.common.security.template.dto.Z9CrudDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class GroupUserDto implements Serializable, Z9CrudDto<GroupUser> {
    private static final long serialVersionUID = -991256606808363446L;

    private Integer rowNum;

    private String errorMsg;

    private String id;

    @NotBlank
    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("父用户id")
    private String parentUserId;

    @ApiModelProperty("id路径")
    private String idPath;


}
