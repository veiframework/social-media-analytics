import com.chargehub.admin.playwright.PlaywrightBrowser;
import com.chargehub.admin.scheduler.DouYinWorkScheduler;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.SneakyThrows;

/**
 * @author Zhanghaowei
 * @since 2025-12-27 9:52
 */
public class PlainClass {


    @SneakyThrows
    public static void main(String[] args) {
        DouYinWorkScheduler.loadLocalCache();
        try (PlaywrightBrowser playwrightBrowser = new PlaywrightBrowser(PlaywrightBrowser.buildProxy())) {
            Page page = playwrightBrowser.newPage();
            page.navigate("https://v.douyin.com/74d1AS59dVc/", new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));
            System.out.println(page.url());
        }
    }

}
