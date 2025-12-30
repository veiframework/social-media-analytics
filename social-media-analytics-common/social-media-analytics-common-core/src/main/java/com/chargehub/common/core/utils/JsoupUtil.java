package com.chargehub.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Slf4j
public class JsoupUtil {

    public static final String SCRIPT = "script";

    private JsoupUtil() {
    }

    public static Document parse(String html) {
        return Jsoup.parse(html, "", Parser.htmlParser());
    }

    public static Document parse(InputStream inputStream) {
        try {
            return Jsoup.parse(inputStream, null, "", Parser.htmlParser());
        } catch (IOException e) {
            throw new IllegalArgumentException("html内容解析失败");
        }
    }

    public static List<String> findContentsInScript(Document document, String keyword) {
        return document.select(SCRIPT).stream().filter(i -> i.html(new StringBuilder()).toString().contains(keyword))
                .map(script -> script.html().replace(keyword, "")).collect(Collectors.toList());
    }

    public static String findContentInScript(Document document, String keyword) {
        Element script = document.select(SCRIPT).stream().filter(i -> i.html(new StringBuilder()).toString().contains(keyword))
                .findFirst().orElse(null);
        if (script == null) {
            return null;
        }
        return script.html().replace(keyword, "");
    }

    public static String findContentInScript(InputStream inputStream, String keyword) {
        Document document = parse(inputStream);
        return findContentInScript(document, keyword);
    }

    public static String findContent(InputStream inputStream, String pattern) {
        Document document = parse(inputStream);
        return findContent(document, pattern);
    }

    public static String findContent(Document document, String pattern) {
        Elements elements = document.getElementsMatchingOwnText(pattern);
        return elements.text().replace(pattern, "");
    }
}
