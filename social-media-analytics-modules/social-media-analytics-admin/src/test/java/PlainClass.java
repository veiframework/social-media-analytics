import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.common.core.utils.JsoupUtil;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import org.springframework.util.Assert;

import java.io.InputStream;

/**
 * @author Zhanghaowei
 * @since 2025-12-27 9:52
 */
public class PlainClass {


    @SneakyThrows
    public static void main(String[] args) {
        String s = FileUtil.readUtf8String("D:\\ideaProject\\social-media-analytics\\social-media-analytics-modules\\social-media-analytics-admin\\src\\main\\resources\\douyin.json");
        JsonNode jsonNode = JacksonUtil.toObj(s);
        JsonNode cookies = jsonNode.get("cookies");
        StringBuilder collect = new StringBuilder();
        cookies.forEach(k -> {
            collect.append(k.get("name").asText()).append("=").append(k.get("value")).append(";");
        });
        System.out.println("--------------------------------");
        System.out.println(collect);
        HttpRequest get = HttpUtil.createGet("http://xhslink.com/o/58YhtOXdUf7")
                .headerMap(BrowserConfig.BROWSER_HEADERS, true);
        get.setFollowRedirects(true);
        HttpResponse execute = get.execute();
        InputStream inputStream = execute.bodyStream();
        String globalJson = JsoupUtil.findContentInScript(inputStream, "window.__INITIAL_STATE__=", "").replace("undefined", "null");
        System.out.println(globalJson);
        Assert.hasText(globalJson, "can not be null");
        JsonNode node = JacksonUtil.toObj(globalJson).at("/note/noteDetailMap");
        JsonNode detailNode = node.get(node.fieldNames().next());
        JsonNode noteNode = detailNode.get("note");
    }

}
