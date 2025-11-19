package com.chargehub.thirdparty.api.domain.dto.baidu;

import lombok.Data;
import lombok.ToString;

/**
 * 由sessionKey获取手机号
 */
@Data
@ToString
public class BaiduPhoneNoDTO {

    private String sessionKey;

    private String encryptedData;

    private String ivStr;

}
