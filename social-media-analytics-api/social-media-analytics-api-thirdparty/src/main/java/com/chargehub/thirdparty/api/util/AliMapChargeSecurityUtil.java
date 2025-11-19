package com.chargehub.thirdparty.api.util;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * @author Zhanghaowei
 * @date 2024/07/29 15:52
 */
public class AliMapChargeSecurityUtil {

    private AliMapChargeSecurityUtil() {
    }

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * 商家请求高德（商家推送数据给高德）
     */
    public static void main(String[] args) throws Exception {
        String merchantPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCVW3pqMOlTPFRgkZfulbhbS3+w1RYRPKtikxWAR1Yckgf71C2CmUKwKTlK8LD+GaJJ227joUO/wf0p4Wq545NDkz2J8bn4L8LobCHNCtCsb/NFhiHm/im8FK3H9L4dZ6DxdAv1sirUDo0gvEpGIRKM2kuSLkI9+OW4JHIo0qMWWEwJEFPIRozpfxJCGlOSDAAk8e+YggxElaknSK0wH7L9bzZkoPMhYyEUJDFEL/WiHtmzuXV2JH7rnVMFHvn5g1WwXHtGwg4FcCDzT3Lho9P7ZQELYjPWue0J8ezhbSYOqH3MSQXs9B+F6Lqz3bP3jDph/doMwW7H0KO5P9FEtfbZAgMBAAECggEBAIZuFsl7ipbDZ0Kf6LrygUOSubSEpl8PV0bbEfAqxQPkIFF2kRvKLC74e7vK1dNvaRXG7pDtdlgjF2VZocD8Z3DcFviQ3IzZGDowEWBHCrkZ7kE2UE9jllbV2fBuhatbuY15oMYc/dVt33KGZuemHtcuJ/TDtBWpb8IE88dwhx+1H/z9qzXloO4ljZZu2nc/RmCsTrywu+/Jz9sbFLO8ZtWNpLjnXJSDmDxX9d0BTiGCk/AEkdqr5coahDpqYyMi5kNZe5kLmZLc5udltp/xGUzEBnGQb2OsLCXKxQVhtcOPYqfk6JyCBPQHLHh6v8/5BEoa3tNjymstmNFiYWF9cQECgYEA/GMtPleJAo3stri4K3nbYzUy1a313814aLsDcBtbiR7WSZVe9DG6HTxMZ7qN9z+ygyBwg4oiMMmoyCI0ugZ7EfoCKUp0+hj51kUYA9mHVhnqQ6y2oIEyBCRmxPjH2h9zjmP1m+8c4yXR8fPMJRDCdiWUKUrbEx5vDVfFKxdG3TkCgYEAl37EsLZYVFkfM+iibnqsRKmREwvMUdI7fVNeQCDosdcLNji9Wihr6OWPhIdFaL+dsKXvkIPob+orGBhkuHILdzRhpFegJVru5BZgcjA1jcdQAxn03CURliAf2yXJtUSU17J9FXye5Isfj8x62Afb9nhiwodBgrii0jGjmy0hhqECgYEA0ChsFFLV6YlshndrTlA+pWAnAR1tAJzShm5Xs4qcwEbyrdXIbtbkjWBCE/tFQ3sBnCyRH6w57ssjrYyouBTIRFdSmQOT3wHibNqnp+UtPdI56H72UQK9YbU0HZiiHEB8ah//XhCkXqO+uDM/WAKuoAr2W+glL2uWZAbT02prQ+ECgYBkciP9utk0C+dZZoyUTff8iqVrCG6PbaIOBYG/aVJ/38JKUDKUHar2v4Z566rEug/W+R6vJhauZYfvMPVPKiaaAfwfmA3R1soAJyoJbBeGCrntZH9qm5Vegaw9hH+KFY/OuEShQ4Cire/eS0jEBen9MHLi4gU4pC0oysrcb38EIQKBgFZwaHSxL+Cy843/2ZwddHxc/vBnk1zpBeCe0+xmicIb3ATRKCscMLXExLMYQ46ZyvyxNyi9zlyOhQvtkYMbe3XdYI5FIrMCMAA2sK7A+ukOynryowvQqe22i+IcWLH3IPZ9/eBdl/No0X+U7Z6Sbi84S6flDcNWJIaxks01qg64";
        String amapPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlVt6ajDpUzxUYJGX7pW4W0t/sNUWETyrYpMVgEdWHJIH+9QtgplCsCk5SvCw/hmiSdtu46FDv8H9KeFqueOTQ5M9ifG5+C/C6GwhzQrQrG/zRYYh5v4pvBStx/S+HWeg8XQL9bIq1A6NILxKRiESjNpLki5CPfjluCRyKNKjFlhMCRBTyEaM6X8SQhpTkgwAJPHvmIIMRJWpJ0itMB+y/W82ZKDzIWMhFCQxRC/1oh7Zs7l1diR+651TBR75+YNVsFx7RsIOBXAg809y4aPT+2UBC2Iz1rntCfHs4W0mDqh9zEkF7PQfhei6s92z94w6Yf3aDMFux9CjuT/RRLX22QIDAQAB";
        Map<String, String> paramMap = new HashMap<>();

        // 构造业务参数，内容请参考具体接口文档
        JSONObject bizContent = new JSONObject();
        bizContent.put("source", "example_api");
        bizContent.put("merchantId", "8888777711119999");
        bizContent.put("shopInfos", new String[]{});
        paramMap.put("biz_content", JSONObject.toJSONString(bizContent));

        // 构造请求公参
        //utc_timestamp时间戳需要最近5分钟内生成（毫秒）
        paramMap.put("utc_timestamp", System.currentTimeMillis() + "");
        //version默认为1.0
        paramMap.put("version", "1.0");
        //charset默认为UTF-8
        paramMap.put("charset", "UTF-8");
        //method即接口名（spi/api），请参考具体接口文档
        paramMap.put("method", "amap.brand.merchant.createShop");
        //请登录高德云店「接入准备及配置」页面获取app_id
        paramMap.put("app_id", "202210130194031937");
        //sign为参数签名，需要根据当前请求的参数动态生成，sign生成方法请参考签名验签帮助文档 https://x-one.amap.com/docs/public/sign_verify
        String signed = generateSign(paramMap, merchantPrivateKey);
        paramMap.put("sign", signed);
        //固定默认为RSA2
        paramMap.put("sign_type", "RSA2");
        boolean checkSign = checkSign(paramMap, amapPublicKey);
        System.out.println(checkSign);
//        // 定义Content-type、初始化HttpEntity
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, Object> formBody = convertToMultiValueMap(paramMap);
//        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(formBody, headers);
//
//        // 调用高德接口
//        // 线下联调环境：https://restapi.amap.com/rest/openmp/devgw?key=高德云店秘钥key
//        // 线上环境：https://restapi.amap.com/rest/openmp/gw?key=高德云店秘钥key
//        // 高德云店秘钥key请登录高德云店「接入准备及配置」页面查看
//        String url = "https://restapi.amap.com/rest/openmp/devgw?key=77ff5350723d29901fa859515f553644";
//        ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
//        if (resp.getBody() != null && !"".equals(resp.getBody())) {
//            JSONObject respObj = JSONObject.parseObject(resp.getBody());
//            System.out.println("高德返回结果:" + respObj);
//        }
    }

    private static MultiValueMap<String, Object> convertToMultiValueMap(Map<String, String> paramMap) {
        MultiValueMap multiValueMap = new LinkedMultiValueMap<String, Object>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            multiValueMap.add(entry.getKey(), entry.getValue());
        }
        return multiValueMap;
    }


    /**
     * 使用商家私钥生成签名
     *
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static String generateSign(Map<String, String> paramMap, String merchantPrivateKey) throws Exception {
        //使用商家私钥进行加签，请在高德云店「接入准备及配置」页面生成并获取商家私钥
        String signContent = getSignContent(paramMap);
        return getSign(signContent, merchantPrivateKey);
    }

    /**
     * 参数转换为待加签字符串
     *
     * @param paramMap 待生成加密sign的参数集合
     */
    private static String getSignContent(Map<String, String> paramMap) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(paramMap.keySet());
        // 将参数集合排序
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            //排除不需要参与签名的公共参数
            if ("sign_type".equals(key) || "sign".equals(key) || "need_encrypt".equals(key)) {
                continue;
            }
            String value = paramMap.get(key);
            // 拼装所有非空参数
            if (key != null && !"".equalsIgnoreCase(key) && value != null && !"".equalsIgnoreCase(value)) {
                content.append(i == 0 ? "" : "&").append(key).append("=").append(value);
            }
        }
        return content.toString();
    }

    /**
     * 字符串加签
     *
     * @param signContent        待加密的参数字符串
     * @param merchantPrivateKey 商家应用私钥
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static String getSign(String signContent, String merchantPrivateKey) throws IOException, GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] encodedKey = readText(new ByteArrayInputStream(merchantPrivateKey.getBytes())).getBytes();
        encodedKey = Base64.getDecoder().decode(encodedKey);
        PrivateKey priKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));

        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(priKey);
        signature.update(signContent.getBytes("UTF-8"));
        byte[] signed = signature.sign();
        return new String(Base64.getEncoder().encode(signed));
    }

    private static String readText(InputStream in) throws IOException {
        Reader reader = new InputStreamReader(in);
        StringWriter writer = new StringWriter();

        int bufferSize = 4096;
        char[] buffer = new char[bufferSize];
        int amount;
        while ((amount = reader.read(buffer)) >= 0) {
            writer.write(buffer, 0, amount);
        }
        return writer.toString();
    }


    /**
     * 高德调用商家api
     */

    /**
     * 使用高德公钥验证签名
     *
     * @param paramFromAmap
     * @return
     * @throws Exception
     */
    public static boolean checkSign(Map<String, String> paramFromAmap, String merchantPublicKey) throws Exception {
        //使用高德公钥进行验签，请在高德云店「API对接信息」页面直接获取高德公钥
        String signContent = getSignContent(paramFromAmap);
        return checkSign(signContent, paramFromAmap.get("sign"), merchantPublicKey);
    }


    /**
     * 对加密字符串进行验签
     *
     * @param content       待验签内容
     * @param sign          待验证签名
     * @param amapPublicKey 高德公钥
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static boolean checkSign(String content, String sign, String amapPublicKey) throws IOException, GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        StringWriter writer = new StringWriter();
        io(new InputStreamReader(new ByteArrayInputStream(amapPublicKey.getBytes())), writer);
        byte[] encodedKey = writer.toString().getBytes();
        encodedKey = org.apache.commons.codec.binary.Base64.decodeBase64(encodedKey);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initVerify(pubKey);
        signature.update(content.getBytes("UTF-8"));
        return signature.verify(Base64.getDecoder().decode(sign.getBytes()));
    }

    public static void io(Reader in, Writer out) throws IOException {
        int bufferSize = 4096;
        char[] buffer = new char[bufferSize];
        int amount;
        while ((amount = in.read(buffer)) >= 0) {
            out.write(buffer, 0, amount);
        }
    }


}
