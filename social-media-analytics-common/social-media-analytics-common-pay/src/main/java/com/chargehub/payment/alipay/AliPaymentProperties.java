package com.chargehub.payment.alipay;

import com.chargehub.payment.bean.BasePaymentProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2025/04/03 15:41
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AliPaymentProperties extends BasePaymentProperties implements Serializable {
    private static final long serialVersionUID = -5034825803208343885L;

    /**
     * 网关地址
     * 线上：https://openapi.alipay.com/gateway.do
     * 沙箱：https://openapi.alipaydev.com/gateway.do
     */
    private String serverUrl = "https://openapi.alipay.com/gateway.do";

    /**
     * 开放平台上创建的应用的ID
     */
    private String appId = "2021004173638349";

    /**
     * 商户私钥
     */
    private String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCUjHwwKJ0M9dNJSdbeq/95+1eqZxBuJs2lBBXXjW/o5pcmdtR9DLVTNZCevrtBRXGxjh0Ds6INZqEp8uMEtydp9/CJdl8Qv/k2KrVjlQGjdyuNM8Nw/aPkVJO8sntSmic0JBpFbOC5nBnPVJtjpRXEYQVGl3UUQ6sm6neg6VFjzCPF4tSl2sC/vvNdi1+326b9KAQx8aW8vvAUcsBhsO8JbILp4dywI2BlrFrNixk79wOKMNsmnulw2xqVxl8siPMIlmE+zvImBvX6TE2hq2LkliHs97CxHGgcJScF2mVEM8CvTbFhMPpQgKgwK5jxYGA/+EcuX9MYE+C783ZPsTlXAgMBAAECggEAX61Yc6BE/0r6ClM/zO3VP9juLvfw8jGV9Hn8I3iaotSaeBDDbcOYqn5upoBMlY7H2seg0niJoL4AgcdKBwh8n1ktmSwfsOck+YlqiTg49PNVnr2/XPBQ9kivWzNOEPbu1zuFnD+zQnlTemGRMA3tRi4DOuNkamfnPcp4wLBmVrLxTudirz/Ze2aWKke4LhwatZDsRO7LMswjGbLAbRe4/YsDL0HUfr29mNk2zHetMOa/Gw5En/Cdw0u+bG9BdL+vIOyIOGA7WsYAJuYgX0OoX+vPG22TqyABssBgcgPc5U7EenPYxZXVEQ7wQ6hVivpXGR6EADdOCO3lnkWfNCMcyQKBgQDbMMMlLS7dreUtqTDGY403sQQfyjTd/9QNNV2A00bDYIJIVPLU2uhdlG/T5c6luy8jvR4+T6WCn741f4dUTSiHwyZzgYFlgHE2rdd4Z3+daFUBYyk9h7uHiFBZsmp6vCQtwdD+IEYCzxSrDyqWoPE+tUykx2aFHEhmzMtzJ1WKUwKBgQCtfsI98OuiqpqGgqp7NnQwZUfpcJqOUbbxLFMmjurNxowitY5Xat16YwKouWw0R8dC+rz3ySg/aiFICeHT3wML7F2ydPc2hV9wDQkPcMpFMykie9EiziRaF7iqmL/jAQr79L6ikRi0D1ZgyBc9w+bnWPERt3avp7/p3VSajELcbQKBgQDOiOcIi8eiWG4flVIBTeLN/D1PmyAvoxTzkTpW3j3KcZCxiWH7EX5sVyb8Ool6fqOPgG8T9caWC+GV1WH2HHI5YrhDlF8R/nIvPKVQre2Aq8OVeBNvCAu7TMWxjz1oAwnwF7XPDygJE4l9GeBeaK6JERp0FfPTL3BFdWmrSv/+QwKBgGqhSJK3OqucPSLEtjDKE/kWlm8D9QnZcR2jB2vgVLGqAMDoYxaZKIwqxByc8cLkBrTE1FVaPbFT8iaLZE9Kdl3ipOekebf5muGMMXBa+wYy3mpCPRQuLCtu/uFixjSnlG+sSWoy0UMBVi5fa+FbQNQZVvhzkK9+4zXCAViHMiS1AoGAWBl31mbFyOYhutR0iNLbpK23IdrDNDnogZT6ucL//W7o490tKAD1lfbwirL/i9SGehESPyr1eyAKp9n/qMnAKaLDB4c0vjeSV3SeuL7kXVaawCgZrWVRhpDckEoepsGNZ/RZ7G7l1Ht2PhOP/lx03+fRxooNd+2OMdXbyKVNF7s=";

    /**
     * 支付宝公钥字符串（公钥模式下设置，证书模式下无需设置）
     */
    private String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhLrCWJAOEuDlT4kCQInSdcnu5xh138D5Dyov5oggl8b7p/4zZFleaH52L0M20K9M7U0OK8bPfye2OkhspXaLohmMP7O8CdPRJ4Xk2vavbsel9vfgiGX/H/bTfq/0xwIk5GLd/4MY82a5O6jrcdA6kdDQFb2ijSIyL1+s+dvZ2TdKkwNdagYzWXNriKYx2RYyHnKFuZNVRX81jfutckgvPTDGhrHbKomwq+tCu7xQCqaUOye5eFy+E3NZqopSnpCyFvTSFO2aSCu4LiL6ycKYFGdYGcdlQiYe1mCpj9YNyIx/+0COG3pfZetk9XTRUdZlX7X2Z82ZSs7M7ZIjQ1CN/wIDAQAB";


    /**
     * 报文格式，推荐：json
     */
    private String format = "json";

    /**
     * 字符串编码，推荐：utf-8
     */
    private String charset = "utf-8";

    /**
     * 签名算法类型，推荐：RSA2
     */
    private String signType = "RSA2";


    /**
     * 商户应用公钥证书路径（证书模式下设置，公钥模式下无需设置）
     */
    private String appCertPath;

    /**
     * 支付宝公钥证书路径（证书模式下设置，公钥模式下无需设置）
     */
    private String alipayPublicCertPath;

    /**
     * 支付宝根证书路径（证书模式下设置，公钥模式下无需设置）
     */
    private String rootCertPath;

    /**
     * 指定商户公钥应用证书内容字符串，该字段与appCertPath只需指定一个，优先以该字段的值为准（证书模式下设置，公钥模式下无需设置）
     */
    private String appCertContent;

    /**
     * 指定支付宝公钥证书内容字符串，该字段与alipayPublicCertPath只需指定一个，优先以该字段的值为准（证书模式下设置，公钥模式下无需设置）
     */
    private String alipayPublicCertContent;

    /**
     * 指定根证书内容字符串，该字段与rootCertPath只需指定一个，优先以该字段的值为准（证书模式下设置，公钥模式下无需设置）
     */
    private String rootCertContent;

    /**
     * 敏感信息对称加密算法类型，推荐：AES
     */
    private String encryptType = "AES";

    /**
     * 敏感信息对称加密算法密钥
     */
    private String encryptKey;

    /**
     * HTTP代理服务器主机地址
     */
    private String proxyHost;

    /**
     * HTTP代理服务器端口
     */
    private int proxyPort;

    /**
     * 自定义HTTP Header
     */
    private Map<String, String> customHeaders;

    /**
     * 连接超时，单位：毫秒
     */
    private int connectTimeout = 3000;

    /**
     * 读取超时，单位：毫秒
     */
    private int readTimeout = 15000;

    /**
     * 连接池最大空闲连接数
     */
    private int maxIdleConnections = 0;

    /**
     * 存活时间，单位：毫秒
     */
    private long keepAliveDuration = 10000L;


    /**
     * 子商户的收款支付宝合作伙伴PID,进入 密钥管理 中的 mapi网关产品密钥 页面，以 2088 开头的 ID 即为合作伙伴身份 PID
     */
    private String sellerId;


    public String getNotifyUrl(String id, String businessTypeCode) {
        return this.buildNotifyUrl(this.getNotifyUrl(), "alipay", id, businessTypeCode);
    }


}
