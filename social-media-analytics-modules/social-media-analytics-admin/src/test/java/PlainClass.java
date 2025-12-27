import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.common.core.utils.JsoupUtil;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;

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
        HttpRequest get = HttpUtil.createGet("https://v.kuaishou.com/7GK5P2ic")
                .headerMap(BrowserConfig.BROWSER_HEADERS, true);
        get.setFollowRedirects(true);
        HttpResponse execute = get.execute();
        System.out.println(execute.body());
        String json = JsoupUtil.findContentInScript(execute.bodyStream(), "window.INIT_STATE = ", "");
        JsonNode obj = JacksonUtil.toObj(json);

        JsonNode jsonNode0 = null;
        int idx = 0;
        for (JsonNode node : obj) {
            if (idx == 2) {
                jsonNode0 = node;
                break;
            }
            idx++;
        }
        JsonNode countsNode = jsonNode0.get("counts");
        System.out.println(countsNode);
    }

}
