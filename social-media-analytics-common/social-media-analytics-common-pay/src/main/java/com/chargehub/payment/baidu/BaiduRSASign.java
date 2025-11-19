package com.chargehub.payment.baidu;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * 百度交易中台双向RSA签名工具
 * JDK版本要求：1.8+
 */
public class BaiduRSASign {

    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE_RSA = "RSA";
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String SIGN_KEY = "rsaSign";

    /**
     * 使用私钥生成签名字符串
     *
     * @param params     待签名参数集合
     * @param privateKey 私钥原始字符串
     *
     * @return 签名结果字符串
     *
     * @throws Exception
     */
    public static String sign(Map<String, Object> params, String privateKey) throws Exception {
        isTrue(!CollectionUtils.isEmpty(params), "params is required");
        notNull(privateKey, "privateKey is required");

        String signContent = signContent(params);

        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(getPrivateKeyPKCS8(privateKey));
        signature.update(signContent.getBytes(CHARSET));
        byte[] signed = signature.sign();

        return new String(Base64.getEncoder().encode(signed));
    }

    /**
     * 使用公钥校验签名
     *
     * @param params    入参数据，签名属性名固定为rsaSign
     * @param publicKey 公钥原始字符串
     *
     * @return true 验签通过 | false 验签不通过
     *
     * @throws Exception
     */
    public static boolean checkSign(Map<String, Object> params, String publicKey) throws Exception {
        isTrue(!CollectionUtils.isEmpty(params), "params is required");
        notNull(publicKey, "publicKey is required");

        // sign & content
        String content = signContent(params);
        String rsaSign = params.get(SIGN_KEY).toString();

        // verify
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initVerify(getPublicKeyX509(publicKey));
        signature.update(content.getBytes(CHARSET));

        return signature.verify(Base64.getDecoder().decode(rsaSign.getBytes(CHARSET)));
    }

    /**
     * 对输入参数进行key过滤排序和字符串拼接
     *
     * @param params 待签名参数集合
     *
     * @return 待签名内容
     *
     * @throws UnsupportedEncodingException
     */
    private static String signContent(Map<String, Object> params) throws UnsupportedEncodingException {
        Map<String, String> sortedParams = new TreeMap<>(Comparator.naturalOrder());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if (legalKey(key)) {
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

    /**
     * 将公钥字符串进行Base64 decode之后，生成X509标准公钥
     *
     * @param publicKey 公钥原始字符串
     *
     * @return X509标准公钥
     *
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    private static PublicKey getPublicKeyX509(String publicKey) throws InvalidKeySpecException,
            NoSuchAlgorithmException, UnsupportedEncodingException {
        if (StringUtils.isEmpty(publicKey)) {
            return null;
        }
        KeyFactory keyFactory = KeyFactory.getInstance(SIGN_TYPE_RSA);
        byte[] decodedKey = Base64.getDecoder().decode(publicKey.getBytes(CHARSET));
        return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
    }

    /**
     * 将私钥字符串进行Base64 decode之后，生成PKCS #8标准的私钥
     *
     * @param privateKey 私钥原始字符串
     *
     * @return PKCS #8标准的私钥
     *
     * @throws Exception
     */
    private static PrivateKey getPrivateKeyPKCS8(String privateKey) throws Exception {
        if (StringUtils.isEmpty(privateKey)) {
            return null;
        }
        KeyFactory keyFactory = KeyFactory.getInstance(SIGN_TYPE_RSA);
        byte[] decodedKey = Base64.getDecoder().decode(privateKey.getBytes(CHARSET));
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
    }

    /**
     * 有效的待签名参数key值
     * 非空、且非签名字段
     *
     * @param key 待签名参数key值
     *
     * @return true | false
     */
    private static boolean legalKey(String key) {
        return StringUtils.hasText(key) && !SIGN_KEY.equalsIgnoreCase(key);
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("appKey", "MMnrvz");
        map.put("dealId", "2047627935");
        map.put("tpOrderId", "3f82afeb9e6f49bf99a3e8b1cf54904k");
        map.put("totalAmount", "1");
        String privateKey2 = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAM+LRmZVgPU4onykEOb5ktdcQwQq9Lj40wUx7DumBQHJ6ROnduM/G6yN4RnbvHxLmnmcACbF3rAzUh+yUAtNN1YbfjnIWLCxaPR7Vq37rQ4X+lyHVSuq6HLGxNTfgbXauU4iRYURfEcu8M9lgaCWCRkFWoPWkMRBCqIFlGrshlfxAgMBAAECgYAF918CuhqG8iXZp23E9g++mSxkBkgauSx3zcOYw2EJeCB3LC1CwyGsiDecYrC8cf/y1wU6GB4aup71wk6CxKl+huO+mczU0mbNNUFkaeTEYMPHpCCXWnQ906Hk70L709vQozPVja3daIv15gFQGb1TZcqlIA0+w74tt9vF6Fwy8QJBAP2AQoWn7i5j1yH/ScdlQHYnK+w3TdbtfknIW4F3JMF20s4lsVOI+Ecuexe1ZW8c5Axzf4aCTiQeBnQuzP+iBScCQQDRlwmHy5RqnM+eBAFhO4l7wb3vVNVC6hFP6V+qJVLAK5YYRcVnm+y+Q+Y+r66CCn1hhmqsIjBoWJINHizaq1knAkEA3Z/UiKVYRpZi7wYRqpGK2cFFVS7EjrLzRe40ketn5vvLMQ8jCLUm2pR4aTHdjFblyTzwsNgbGNhnLAv+jT9I4QJBAIYh2TGU8hkA3kZMXe2yQpx3M2z1p745Oncf7d9gH4No4ZgLHvjd6zchETtRB6eluvQtKBudjclZ1a6bfy06t8UCQQCqlT7b/l2wL9fwndJZVPJk4dBoQS5yTaFnuLYvWviwzriOnEZKwGBDYJyKE50/mQg6ANj0AgiUmjpItuah4oYN";
        String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAM+LRmZVgPU4onyk" +
                "EOb5ktdcQwQq9Lj40wUx7DumBQHJ6ROnduM/G6yN4RnbvHxLmnmcACbF3rAzUh+y" +
                "UAtNN1YbfjnIWLCxaPR7Vq37rQ4X+lyHVSuq6HLGxNTfgbXauU4iRYURfEcu8M9l" +
                "gaCWCRkFWoPWkMRBCqIFlGrshlfxAgMBAAECgYAF918CuhqG8iXZp23E9g++mSxk" +
                "BkgauSx3zcOYw2EJeCB3LC1CwyGsiDecYrC8cf/y1wU6GB4aup71wk6CxKl+huO+" +
                "mczU0mbNNUFkaeTEYMPHpCCXWnQ906Hk70L709vQozPVja3daIv15gFQGb1TZcql" +
                "IA0+w74tt9vF6Fwy8QJBAP2AQoWn7i5j1yH/ScdlQHYnK+w3TdbtfknIW4F3JMF2" +
                "0s4lsVOI+Ecuexe1ZW8c5Axzf4aCTiQeBnQuzP+iBScCQQDRlwmHy5RqnM+eBAFh" +
                "O4l7wb3vVNVC6hFP6V+qJVLAK5YYRcVnm+y+Q+Y+r66CCn1hhmqsIjBoWJINHiza" +
                "q1knAkEA3Z/UiKVYRpZi7wYRqpGK2cFFVS7EjrLzRe40ketn5vvLMQ8jCLUm2pR4" +
                "aTHdjFblyTzwsNgbGNhnLAv+jT9I4QJBAIYh2TGU8hkA3kZMXe2yQpx3M2z1p745" +
                "Oncf7d9gH4No4ZgLHvjd6zchETtRB6eluvQtKBudjclZ1a6bfy06t8UCQQCqlT7b" +
                "/l2wL9fwndJZVPJk4dBoQS5yTaFnuLYvWviwzriOnEZKwGBDYJyKE50/mQg6ANj0" +
                "AgiUmjpItuah4oYN";
        String signStr = sign(map, privateKey2);
        System.out.println(signStr);

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2Vs9DSJo6ttXQSYoxaJalPbIvWWWv7UsHo4shvI4Lrz9OU0axF+Ckup5V9nt2MmsnEjgVQdWZjZezC3erCpBic11j2eBhgcDtvBxfuqvTaMiW8x0cfnhlHw8RdPYO1Sle0LqkD834cO8ylD9VzanzZofyINbwXuWopl6a5AKfawIDAQAB";
        String content = "count=1&dealId=2047627935&giftCardMoney=0&hbBalanceMoney=0&hbMoney=0&orderId=116770487492733&partnerId=6000001&payMoney=1&payTime=1726561412&payType=1087&promoDetail=&promoMoney=0&returnData=&status=2&totalMoney=1&tpOrderId=20240917162309596896576109655794&unitPrice=1&userId=59487190";
        String rsaSign = "l3KOE0/KZby+DRVQV7JPS5oF8bqGJODZ6s0QhnGHun6ZEgcoNGL5jHgUhT89vZTjXtbCx3nReuTA+slhyg/BpMQ5anu8FdghdAKfc0haELY3HLossAZwfIHlao8SnLEUabHJhHSsCyFMIQHdksvKe0vlzcCs7JM8NSMbSHKUJ7Y";
        // verify
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initVerify(getPublicKeyX509(publicKey));
        signature.update(content.getBytes(CHARSET));
        System.out.println(signature.verify(Base64.getDecoder().decode(rsaSign.getBytes(CHARSET))));

        String publicKey2 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2Vs9DSJo6ttXQSYoxaJalPbIvWWWv7UsHo4shvI4Lrz9OU0axF+Ckup5V9nt2MmsnEjgVQdWZjZezC3erCpBic11j2eBhgcDtvBxfuqvTaMiW8x0cfnhlHw8RdPYO1Sle0LqkD834cO8ylD9VzanzZofyINbwXuWopl6a5AKfawIDAQAB";
        String content2 = "count=1&dealId=2047627935&giftCardMoney=0&hbBalanceMoney=0&hbMoney=0&orderId=116770487492733&partnerId=6000001&payMoney=1&payTime=1726561412&payType=1087&promoDetail=&promoMoney=0&returnData=&status=2&totalMoney=1&tpOrderId=20240917162309596896576109655794&unitPrice=1&userId=59487190";
        String rsaSign2 = "l3KOE0/KZby+DRVQV7JPS5oF8bqGJODZ6s0QhnGHun6ZEgcoNGL5jHgUhT89vZTjXtbCx3nReuTA+slhyg/BpMQ5anu8FdghdAKfc0haELY3HLossAZwfIHlao8SnLEUabHJhHSsCyFMIQHdksvKe0vlzcCs7JM8NSMbSHKUJ7Y";
        // verify
        Signature signature2 = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initVerify(getPublicKeyX509(publicKey2));
        signature.update(content2.getBytes(CHARSET));
        signature.verify(Base64.getDecoder().decode(rsaSign2.getBytes(CHARSET)));
    }

}
