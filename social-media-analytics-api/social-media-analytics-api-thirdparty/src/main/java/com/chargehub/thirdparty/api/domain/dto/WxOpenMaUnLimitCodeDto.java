package com.chargehub.thirdparty.api.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:14
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.dto
 * @Filename：WxOpenMaUnLimitCodeDto
 */
@Data
public class WxOpenMaUnLimitCodeDto {

    @NotEmpty
    private String scene;
    @NotEmpty
    private String page;
    private Boolean checkPath;
    private String envVersion;



    private Integer width;
    private Boolean autoColor;
    private Boolean isHyaline;
}
