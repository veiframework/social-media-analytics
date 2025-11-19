package com.chargehub.thirdparty.util;


import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;


/**
 * 常用加密算法工具类 CuiWenJing
 *
 * @author cq
 */

public class EncryptUtils {


    private static final String HTJS_CRYPT_KEY = "htjsyfzx";
    private static final String DES                = "DES";
    private static final String DES_KEY="htjs_zwzx*20191111111111";
    
    /**
     * 用MD5算法进行加密
     *
     * @param str
     *            需要加密的字符串
     * @return MD5加密后的结果
     */
    public static String encodeMD5String(String str) {
        return encode(str, "MD5");
    }

    /**
     * 用SHA算法进行加密
     *
     * @param str
     *            需要加密的字符串
     * @return SHA加密后的结果
     */
    public static String encodeSHAString(String str) {
        return encode(str, "SHA");
    }

    /**
     * 用base64算法进行加密
     *
     * @param str
     *            需要加密的字符串
     * @return base64加密后的结果
     */
    public static String encodeBase64String(String str) {

        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    /**
     * 用base64算法进行解密
     *
     * @param str
     *            需要解密的字符串
     * @return base64解密后的结果
     * @throws IOException
     *             出错
     */
    public static String decodeBase64String(String str){
        return new String(Base64.getDecoder().decode(str));
    }
    
    public static String decodeBase64StringGbk(String str){
    	try {
			return new String(Base64.getDecoder().decode(str),"gbk");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
    }

    private static String encode(String str, String method) {
        MessageDigest md;
        String dstr = null;
        try {
            md = MessageDigest.getInstance(method);
            md.update(str.getBytes());
            dstr = byte2hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
         //  Log.error(EncryptUtils.class,e.getMessage());
        }
        return dstr;
    }

    /**
     * 加密
     *
     * @param src
     *            数据源
     * @param key
     *            密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     * @throws Exception
     *             出错
     */

    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据建立 DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 建立一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙原始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(src);
    }

    /**
     * 解密
     *
     * @param src
     *            数据源
     * @param key
     *            密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     *             出错
     */

    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密匙数据建立一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 建立一个密匙工厂，然后用它把DESKeySpec对象转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙原始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        // 现在，获取数据并解密
        // 正式执行解密操作
        return cipher.doFinal(src);
    }

    /**
     * 密码解密
     *
     * @param data
     *            需要解密的字符串
     * @return 解密的字符串
     */

    public static String decrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data.getBytes()), HTJS_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
            //Log.error(EncryptUtils.class,e.getMessage());
        }
        return null;
    }

    /**
     * 密码加密
     *
     * @param str
     *            作废
     * @return 返回
     */

    public static String encrypt(String str) {
        try {
            return byte2hex(encrypt(str.getBytes(), HTJS_CRYPT_KEY.getBytes()));
        } catch (Exception e) {
           //Log.error(EncryptUtils.class,e.getMessage());
        }
        return null;
    }

    /**
     * 二行制转字符串
     *
     * @param b
     *            需要转换的
     * @return 返回
     */

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp;
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            hs = stmp.length() == 1 ? (hs + "0" + stmp) : (hs + stmp);
        }
        return hs.toUpperCase();
    }

    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0){
            throw new IllegalArgumentException("长度不是偶数");
        }

        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    public static String signContent(Map<String, Object> params) {
        Map<String, String> sortedParams = new TreeMap<>(Comparator.naturalOrder());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if (legalKey(key) && Objects.nonNull(entry.getValue())) {
                sortedParams.put(key, entry.getValue().toString());
            }
        }

        StringBuilder builder = new StringBuilder();
        if (!CollectionUtils.isEmpty(sortedParams)) {
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(entry.getValue());
                builder.append("&");
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    private static boolean legalKey(String key) {
        return StringUtils.hasText(key) && !"sign".equalsIgnoreCase(key);
    }






}