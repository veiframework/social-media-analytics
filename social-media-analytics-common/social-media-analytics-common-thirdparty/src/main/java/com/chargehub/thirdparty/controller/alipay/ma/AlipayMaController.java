package com.chargehub.thirdparty.controller.alipay.ma;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipayEncrypt;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.chargehub.common.core.utils.bean.BeanUtils;
import com.chargehub.thirdparty.api.domain.dto.alipay.AliPayPhoneNoDto;
import com.chargehub.thirdparty.api.domain.vo.alipay.AlipayPhoneNoVo;
import com.chargehub.thirdparty.api.domain.vo.alipay.AlipaySystemOauthTokenVo;
import com.chargehub.thirdparty.config.alipay.AliPayConf;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 16:10
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.controller.ali.ma
 * @Filename：AliPayMaController
 */
@RestController
@RequestMapping("/alipay-ma")
@Tag(name = "支付宝小程序相关", description = "支付宝小程序相关")
public class AlipayMaController {

    @Value("${alipay.decryptKey}")
    private String decryptKey;


    private static final Logger log = LoggerFactory.getLogger(AlipayMaController.class);


    /**
     * 支付宝授权登录
     * @param code
     * @return
     */
    @GetMapping("/auth")
    public AlipaySystemOauthTokenVo auth(@RequestParam(value="code", required = true) String code) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(AliPayConf.getAlipayConfig());
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            request.setGrantType("authorization_code");
            request.setCode(code);
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            log.info("支付宝授权登录响应结果：{}", JSONObject.toJSONString(response));
            if(response.isSuccess()){
                AlipaySystemOauthTokenVo alipaySystemOauthTokenVo = new AlipaySystemOauthTokenVo();
                BeanUtils.copyProperties(response,alipaySystemOauthTokenVo);
                return alipaySystemOauthTokenVo;
            }else{
                log.info("支付宝授权登录失败，响应结果：{}", JSONObject.toJSONString(response));
                return null;
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return null;
        }
    }




    /**
     * 解析支付宝手机号
     * @param phoneNoDto
     * @return
     */
    @PostMapping("/phone-no")
    public AlipayPhoneNoVo decryptionPhoneNo(@RequestBody AliPayPhoneNoDto phoneNoDto){
        try {
            //1. 获取验签和解密所需要的参数
            Map<String, String> openapiResult = JSON.parseObject(phoneNoDto.getResponse(),new TypeReference<Map<String, String>>() {}, Feature.OrderedField);
            AlipayConfig alipayConfig = AliPayConf.getAlipayConfig();

            String signType = alipayConfig.getSignType();
            String charset = alipayConfig.getCharset();
            String encryptType = "AES";
            String sign = openapiResult.get("sign");
            String content = openapiResult.get("response");
            //判断是否为加密内容
            Boolean isDataEncrypted = !content.startsWith("{");
            Boolean signCheckPass = false;
            //2. 验签
            String signContent = content;
            String signVeriKey = alipayConfig.getAlipayPublicKey();
            String decryptKey = this.decryptKey;
            if (isDataEncrypted) {
                signContent = "\"" + signContent + "\"";
            } try {
                signCheckPass = AlipaySignature.rsaCheck(signContent, sign, signVeriKey, charset, signType);
            } catch (AlipayApiException e) {
                // 验签异常, 日志
            } if (!signCheckPass) {
                //验签不通过（异常或者报文被篡改），终止流程（不需要做解密）
                throw new Exception("验签失败");
            }
            //3. 解密
            String plainData = null;
            if (isDataEncrypted) {
                try {
                    plainData = AlipayEncrypt.decryptContent(content, encryptType, decryptKey, charset);
                } catch (AlipayApiException e) {
                    e.printStackTrace();
                    //解密异常, 记录日志
                    throw new Exception("解密异常");
                }
            } else {
                plainData = content;
            }

            AlipayPhoneNoVo alipayPhoneNoVo = JSONObject.parseObject(plainData, AlipayPhoneNoVo.class);
            return alipayPhoneNoVo;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
