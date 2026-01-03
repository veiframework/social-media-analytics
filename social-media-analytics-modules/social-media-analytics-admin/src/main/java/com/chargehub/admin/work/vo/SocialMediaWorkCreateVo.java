package com.chargehub.admin.work.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class SocialMediaWorkCreateVo implements Serializable, Z9CrudVo {
    private static final long serialVersionUID = 319050322425872538L;

    private String id;

    @ApiModelProperty("作品ID")
    private String workId;

    @ApiModelProperty("分享链接")
    private String shareLink;

    @ApiModelProperty("账号类型")
    private String accountType;

    @ApiModelProperty("业务类型")
    private String customType;

    @ApiModelProperty("错误堆栈")
    private String errorStack;

    @ApiModelProperty("错误信息")
    private String errorMsg;

    @ApiModelProperty("创建状态")
    private String createStatus;

    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("重试次数")
    private Integer retryCount;
}
