package com.chargehub.thirdparty.util;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

@SuppressWarnings("DuplicatedCode")
public class RSA {

    /**
     * 非对称密钥算法
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }


    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        //数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return encrypt(data, cipher);
    }

    /**
     * 私钥签名
     *
     * @param data       data
     * @param privateKey privateKey
     * @return String
     */
    public static String sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initSign(privateKey);
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * 验证签名
     *
     * @param data      data
     * @param publicKey publicKey
     * @param sign      sign
     * @return boolean
     * @throws Exception Exception
     */
    public static boolean verify(byte[] data, PublicKey publicKey, String sign) throws Exception {
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

    /**
     * 解决大于数据过长加密失败
     * @param data data
     * @param cipher cipher
     * @return byte[]
     */
    @SneakyThrows
    private static byte[] encrypt(byte[] data, Cipher cipher) {
        int inputLength = data.length;
        // 最大加密字节数，超出最大字节数需要分组加密
        int MAX_ENCRYPT_BLOCK = 117;
        // 标识
        int offSet = 0;
        byte[] resultBytes = {};
        byte[] cache = {};
        while (inputLength - offSet > 0) {
            if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                offSet += MAX_ENCRYPT_BLOCK;
            } else {
                cache = cipher.doFinal(data, offSet, inputLength - offSet);
                offSet = inputLength;
            }
            resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
            System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
        }
        return resultBytes;
    }
}
