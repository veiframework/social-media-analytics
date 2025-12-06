package com.chargehub.admin.account.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaTransferAccountDto implements Serializable {
    private static final long serialVersionUID = -2705653326335535468L;

    @NotBlank
    private String userId;

    @NotBlank
    private String accountId;

}
