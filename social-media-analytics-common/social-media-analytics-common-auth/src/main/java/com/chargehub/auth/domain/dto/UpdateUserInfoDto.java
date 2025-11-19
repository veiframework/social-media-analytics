package com.chargehub.auth.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/5/2 16:45
 * @Project：chargehub
 * @Package：com.chargehub.auth.domain.dto
 * @Filename：UpdateUserInfoDto
 */
@Data
public class UpdateUserInfoDto {

    @ApiModelProperty(value = "使用的头像文件")
    private String picture;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "我的生日")
    private Date birthday;

}
