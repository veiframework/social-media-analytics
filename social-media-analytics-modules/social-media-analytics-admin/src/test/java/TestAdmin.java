import com.chargehub.admin.AdminApplication;

import com.chargehub.admin.datasync.DataSyncManager;
import com.chargehub.admin.datasync.domain.SocialMediaWorkDetail;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.biz.region.service.impl.RegionInitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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
        SocialMediaWorkDetail<SocialMediaWork> socialMediaWorkDetail = this.dataSyncManager.getWork("", "https://www.xiaohongshu.com/discovery/item/682fbc340000000012005b99", new SocialMediaPlatformEnum.PlatformExtra(SocialMediaPlatformEnum.RED_NOTE));
        System.out.println(socialMediaWorkDetail);
    }

}
