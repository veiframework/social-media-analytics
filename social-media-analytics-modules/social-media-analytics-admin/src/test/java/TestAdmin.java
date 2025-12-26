import cn.hutool.core.thread.ThreadUtil;
import com.chargehub.admin.AdminApplication;

import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.SocialMediaWorkDetail;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.biz.region.service.impl.RegionInitService;
import com.chargehub.common.redis.service.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author Zhanghaowei
 * @date 2024/08/05 17:51
 */
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {AdminApplication.class}
)
public class TestAdmin {

    @Autowired
    RegionInitService regionInitService;

    @Test
    public void test(){
        regionInitService.addAddList();
    }

    @Autowired
    private DataSyncManager dataSyncManager;

    @Test
    public void tests(){
        SocialMediaWorkDetail<SocialMediaWork> socialMediaWorkDetail = this.dataSyncManager.fetchWork("", "https://www.douyin.com/note/7567620465126195045", new SocialMediaPlatformEnum.PlatformExtra(SocialMediaPlatformEnum.DOU_YIN));
        System.out.println(socialMediaWorkDetail);
    }

    @Autowired
    private RedisService redisService;

    @Test
    public void testCache(){
        redisService.setHashEx("a","b","1",1, TimeUnit.DAYS);
        redisService.setHashEx("a","c","1",1, TimeUnit.DAYS);
        ThreadUtil.safeSleep(5000);
        redisService.deleteCacheMapValue("a","b");
        ThreadUtil.safeSleep(5000);
        redisService.deleteCacheMapValue("a","c");
    }
}
