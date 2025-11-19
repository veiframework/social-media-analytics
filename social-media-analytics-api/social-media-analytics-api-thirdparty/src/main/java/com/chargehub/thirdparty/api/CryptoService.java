package com.chargehub.thirdparty.api;

import java.io.InputStream;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/08/05 13:49
 */
public interface CryptoService {

    /**
     * 加密
     *
     * @param originBody
     * @return
     */
    byte[] encrypt(Map<String, String> originBody);

    /**
     * 加密
     *
     * @param originBody
     * @return
     */
    byte[] encrypt(byte[] originBody);

    /**
     * 解密
     *
     * @param body
     * @return
     */
    byte[] decrypt(InputStream body);

    /**
     * 解密
     */
    void decrypt();
}
