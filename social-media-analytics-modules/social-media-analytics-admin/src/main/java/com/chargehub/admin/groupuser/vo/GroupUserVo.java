package com.chargehub.admin.groupuser.vo;

import com.chargehub.common.security.annotation.Dict;
import com.chargehub.common.security.template.vo.Z9CrudVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class GroupUserVo implements Z9CrudVo, Serializable {
    private static final long serialVersionUID = 5321766516355730118L;


    private String id;

    @Dict(dictTable = "sys_user", nameColumn = "nick_name", valueColumn = "user_id")
    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("父用户id")
    private String parentUserId;

    @ApiModelProperty("id路径")
    private String idPath;
}
