package com.chargehub.thirdparty.api.domain.dto.baidu;

import lombok.Data;
import lombok.ToString;

/**
 * 由code获取手机号
 */
@Data
@ToString
public class BaiduPhoneNoBizDTO {

    private String code;

    private String encryptedData;

    private String ivStr;
}
