package com.chargehub.auth.domain.vo;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 15:37
 * @Project：chargehub
 * @Package：com.chargehub.auth.domain.vo
 * @Filename：AuthPhoneNoVo
 */
@Data
public class AuthPhoneNoVo {


    /**
     * 纯数字手机号
     */
    private String phoneNumber;

    /**
     * 授权记录id
     */
    private String authId;
}
