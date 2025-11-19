package com.chargehub.gateway.constant;

import com.chargehub.common.core.utils.EncryptUtil;

import java.util.Arrays;
import java.util.List;

public class GatewayConstant {

    public static final String TIME_STAMP = "X-Charge-Time-Stamp";   //时间戳 键值
    public static final String SIGN = "X-Charge-Sign";   //签名 键值
    public static final String SALT = "44ImIvoO2Tr3GDn3a6rfG7IlefjzQZcfLOCNHAQclvr4ozlTzTEDAVDJALfKSKsy";   //签名盐值


    /**
     * 验证签名的请求路径
     */
    public static final List<String> VERIFY_SIGN_PATHS = Arrays.asList(new String[]{
            "/auth-api/app/send-sms"
    });



    public static void main(String[] args) {
        long currentTimeStamp = System.currentTimeMillis();
        System.out.println("时间戳：" + (currentTimeStamp / 1000));

        String md5Sign = EncryptUtil.encrypt(1730786341 + GatewayConstant.SALT,"MD5");
        System.out.println("签名：" + md5Sign);
    }

}
