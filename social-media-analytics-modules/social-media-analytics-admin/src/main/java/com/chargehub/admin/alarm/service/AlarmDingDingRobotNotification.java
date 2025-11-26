package com.chargehub.admin.alarm.service;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.chargehub.common.core.utils.MessageFormatUtils;
import com.chargehub.common.security.utils.JacksonUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
@Service
public class AlarmDingDingRobotNotification implements AlarmNotification {

    @Override
    public String type() {
        return "dingding_robot";
    }

    private static final String TEST_WEBHOOK = "https://oapi.dingtalk.com/robot/send?access_token=ebbedbdee71f2a9e2a0ea4027011c9c3faa53755b165420025d3e18b5c1982bf";
    private static final String secret = "SEC5391f167e4ed87139607ac7a1fc0995cf3871d7697f6762d01d8e1176e331de6";

    @Override
    public void send(AlarmNotificationConfig alarmNotificationConfig) {
        try {
            String token = alarmNotificationConfig.getToken();
            String webhook = alarmNotificationConfig.getWebhook();
            URI uri = URLUtil.toURI(webhook);
            String query = uri.getQuery();
            Map<String, String> paramMap = HttpUtil.decodeParamMap(query, StandardCharsets.UTF_8);
            String accessToken = paramMap.get("access_token");
            String msgTemplate = alarmNotificationConfig.getMsgTemplate();
            List<String> msgFields = alarmNotificationConfig.getMsgFields();
            String formatMsg = MessageFormatUtils.formatMsg(msgTemplate, msgFields);

            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + token;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), CharsetUtil.UTF_8);
            //sign字段和timestamp字段必须拼接到请求URL上，否则会出现 310000 的错误信息
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?sign=" + sign + "&timestamp=" + timestamp);
            OapiRobotSendRequest req = new OapiRobotSendRequest();

            //定义文本内容
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(formatMsg);
            //设置消息类型
            req.setMsgtype("text");
            req.setText(text);
            //定义 @ 对象
            //OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            //at.setAtUserIds(Arrays.asList(USER_ID));
            //req.setAt(at);
            OapiRobotSendResponse rsp = client.execute(req, accessToken);
            String body = rsp.getBody();
            log.debug("发送钉钉消息{}：结果:{}", formatMsg, body);
            JsonNode jsonNode = JacksonUtil.toObj(body);
            Integer errCode = jsonNode.get("errcode").asInt();
            Assert.isTrue(Objects.equals(0, errCode), body);
        } catch (Exception e) {
            log.error("发送告警消息失败:{},{}", e, alarmNotificationConfig);
        }
    }


}
