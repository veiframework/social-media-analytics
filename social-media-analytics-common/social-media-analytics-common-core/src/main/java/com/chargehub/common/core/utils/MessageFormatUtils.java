package com.chargehub.common.core.utils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhanghaowei
 * @date 2024/12/26 10:56
 */
public class MessageFormatUtils {

    private MessageFormatUtils() {
        //nothing to do
    }

    public static String formatMsg(String message, Object... arguments) {
        return MessageFormat.format(message,arguments);
    }

    public static String formatMsg(String message, Collection<?> arguments) {
        return formatMsg(message, arguments.toArray());
    }

    public static void main(String[] args) {
        List<Object> collect = Stream.of("可以", "2", 3, "4").collect(Collectors.toList());
        System.out.println(formatMsg("在 {0} 的充电订单已结束充电，请及时关注。订单尾号：{1}，充电时长：{2} 分钟，消费金额：{3} 元。", collect));
    }

}
