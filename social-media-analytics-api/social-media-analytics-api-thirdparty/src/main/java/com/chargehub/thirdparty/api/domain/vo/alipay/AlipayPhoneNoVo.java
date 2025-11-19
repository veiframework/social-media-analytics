package com.chargehub.thirdparty.api.domain.vo.alipay;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 18:02
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.alipay
 * @Filename：AliPayPhoneNoVo
 */
@Data
public class AlipayPhoneNoVo {

    /**
     * 错误码
     * 10000	成功
     */
    private String code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 手机号
     */
    private String mobile;
}
