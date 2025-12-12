package com.chargehub.admin.work.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaWorkPlayNumDto implements Serializable {
    private static final long serialVersionUID = 992049429639072824L;

    @NotBlank
    private String workId;

    @NotNull
    private Integer playNum;

    @NotBlank
    private String accountId;

}
