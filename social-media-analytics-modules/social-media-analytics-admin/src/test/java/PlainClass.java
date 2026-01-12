import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.datasync.tikhub.DataSyncDouYinServiceImpl;
import com.chargehub.admin.playwright.BrowserConfig;
import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.scheduler.DouYinSyncWorkScheduler;
import com.chargehub.common.core.utils.JsoupUtil;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

/**
 * @author Zhanghaowei
 * @since 2025-12-27 9:52
 */
public class PlainClass {


    @SneakyThrows
    public static void main(String[] args) {
        DouYinSyncWorkScheduler.loadLocalCache();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(PlaywrightBrowser.buildProxy())) {
            Page page = playwrightBrowser.newPage();
            page.navigate("https://v.douyin.com/74d1AS59dVc/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));
            System.out.println(page.url());
        }
    }

    @Test
    public void testDouYin() {
        DouYinSyncWorkScheduler.loadLocalCache();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(PlaywrightBrowser.buildProxy())) {
            DouYinSyncWorkScheduler.navigateToDouYinUserPage(playwrightBrowser);
            Object object = playwrightBrowser.getPage().evaluate(DataSyncDouYinServiceImpl.DOUYIN_FETCH_WORK_JS, "7581889286216207616");
            System.out.println(object);
            playwrightBrowser.getPage().waitForTimeout(600_00000);
        }
    }

    @Test
    public void testRedNote() {
        String noteRq = "https://edith.xiaohongshu.com/api/sns/web/v1/feed";
        String noteBody = "{\"source_note_id\":\"694bc0a7000000001e02f482\",\"image_formats\":[\"jpg\",\"webp\",\"avif\"],\"extra\":{\"need_body_topic\":\"1\"},\"xsec_source\":\"pc_user\",\"xsec_token\":\"ABosVZfLyWkGd6FPxv98xnUC35EtaBrkYsIOmF-TYgaEQ=\"}";
        String profileRq = "https://www.xiaohongshu.com/user/profile/59e0b13c20e88f68e8af29a0";
        HttpRequest httpRequest = HttpUtil.createGet(profileRq)
                .headerMap(BrowserConfig.BROWSER_HEADERS, true);
        try (HttpResponse response = httpRequest.execute()) {
            InputStream uidStream = response.bodyStream();
            String content = JsoupUtil.findContent(uidStream, "小红书号：");
            System.out.println(content);
        }
    }

    @Test
    public void testKuaishou() {
        String commentRq = "https://www.kuaishou.com/graphql";
        String profile = "https://www.kuaishou.com/rest/v/profile/user";
        String profileBody = "{\"user_id\":\"3xgh2i9dfvu23i9\"}";
        String commentBody = "{\"operationName\":\"commentListQuery\",\"variables\":{\"photoId\":\"3xm725ngdnh8hpg\",\"pcursor\":\"\"},\"query\":\"query commentListQuery($photoId: String, $pcursor: String) {\\n  visionCommentList(photoId: $photoId, pcursor: $pcursor) {\\n    commentCount\\n    commentCountV2\\n    pcursor\\n    rootCommentsV2 {\\n      commentId\\n      authorId\\n      authorName\\n      content\\n      headurl\\n      timestamp\\n      hasSubComments\\n      likedCount\\n      liked\\n      status\\n      __typename\\n    }\\n    pcursorV2\\n    rootComments {\\n      commentId\\n      authorId\\n      authorName\\n      content\\n      headurl\\n      timestamp\\n      likedCount\\n      realLikedCount\\n      liked\\n      status\\n      authorLiked\\n      subCommentCount\\n      subCommentsPcursor\\n      subComments {\\n        commentId\\n        authorId\\n        authorName\\n        content\\n        headurl\\n        timestamp\\n        likedCount\\n        realLikedCount\\n        liked\\n        status\\n        authorLiked\\n        replyToUserName\\n        replyTo\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
        HttpRequest httpRequest = HttpUtil.createPost(commentRq)
                .body(commentBody)
                .cookie("kpf=PC_WEB; clientid=3; did=web_505a3785b73d87468952f289a74cb860; kwpsecproductname=kuaishou-vision; kwpsecproductname=kuaishou-vision; kwssectoken=DwlqT+YY4EroHs0daZItyq2g0dnUazUMgRl9rIqC9+n/3Wm5KOmljKcX0uJkIHD/qkukEDo6hlX8eUxhZsXahA==; kwscode=e24d61051f3cf99e525e2dfbaf92ce4023639a2c92e21bfb1e3b2bd02442fbb5; kwfv1=PnGU+9+Y8008S+nH0U+0mjPf8fP08f+98f+nLlwnrIP9+Sw/ZFGfzY+eGlGf+f+e4SGfbYP0QfGnLFwBLU80mYG9rh80LE8eZhPeZFweGIPnH7GnLM+/cFwBpY+fPE8/chG9pYG/zD8eL7+0zY+eHAGnzSPADh80Z9GAclw/p0PAzSP/mjPAH98/LFP/HhweYfG/DhP0HF8fpY+eYj+eYSPI==; kwssectoken=bqTlH5G/S/4y60ldJKXeN8lN+86KFrYZIYmEVWnlZNDo7pDYCs8P82HLRe4Oi1UflWmveQGRq14U0HxsZTYbFg==; kwscode=221e9b74fd391ece5b7009f34d24ccb1e330605d70e7fcab17150fd8c6f4f944; ktrace-context=1|MS44Nzg0NzI0NTc4Nzk2ODY5LjU2Mjg2MTQxLjE3NjY5MjIyMjgzOTMuMjI3MzgyMA==|MS44Nzg0NzI0NTc4Nzk2ODY5Ljg1MTQyODc4LjE3NjY5MjIyMjgzOTMuMjI3MzgyMQ==|0|webservice-user-growth-node|webservice|true|src-Js; kpn=KUAISHOU_VISION")
                .headerMap(BrowserConfig.BROWSER_HEADERS, true);
        try (HttpResponse response = httpRequest.execute()) {
            System.out.println(response.body());
        }
    }

    @Test
    public void testKuaishou2() {
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(PlaywrightBrowser.buildProxy())) {
            Page page = playwrightBrowser.newPage();
            Response navigate = page.navigate("https://v.kuaishou.com/KZbr8k8A", new Page.NavigateOptions().setTimeout(BrowserConfig.LOAD_PAGE_TIMEOUT));
            System.out.println(navigate.text());
            playwrightBrowser.getPage().waitForTimeout(600_00000);
        }
    }

}
