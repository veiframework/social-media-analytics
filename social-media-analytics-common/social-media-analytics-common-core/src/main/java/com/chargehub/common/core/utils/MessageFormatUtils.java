package com.chargehub.common.core.utils;

import cn.hutool.core.text.CharSequenceUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhanghaowei
 * @date 2024/12/26 10:56
 */
public class MessageFormatUtils {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([^\\s#]+)");

    private MessageFormatUtils() {
        //nothing to do
    }

    public static String formatMsg(String message, Object... arguments) {
        return MessageFormat.format(message, arguments);
    }

    public static String formatMsg(String message, Collection<?> arguments) {
        return formatMsg(message, arguments.toArray());
    }

    public static String cleanDescription(String desc) {
        if (StringUtils.isBlank(desc)) {
            return "";
        }
        // 移除所有 #话题 标签
        return desc.replaceAll(HASHTAG_PATTERN.pattern(), "").replace("#","").trim();
    }

    public static List<String> extractHashtags(String desc) {
        if (StringUtils.isBlank(desc)) {
            return Collections.emptyList();
        }
        List<String> hashtags = new ArrayList<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(desc);
        while (matcher.find()) {
            // 获取包含#的话题内容
            String hashtag = matcher.group(1);
            if (StringUtils.isNotBlank(hashtag)) {
                hashtags.add(hashtag.replace("[话题]",""));
            }
        }
        return hashtags;
    }

    public static String extractHashtagsStr(String desc) {
        return String.join(",", extractHashtags(desc));
    }

    public static void main(String[] args) {
        List<Object> collect = Stream.of("可以", "2", 3, "4").collect(Collectors.toList());
        System.out.println(formatMsg("在 {0} 的充电订单已结束充电，请及时关注。订单尾号：{1}，充电时长：{2} 分钟，消费金额：{3} 元。", collect));
        String format = CharSequenceUtil.format("在 {} 的充电订单已结束充电，请及时关注。订单尾号：{}，充电时长：{} 分钟，消费金额：{} 元。", collect.toArray());
        System.out.println(format);
    }
}
