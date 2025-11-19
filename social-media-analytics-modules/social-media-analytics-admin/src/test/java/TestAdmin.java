import com.chargehub.admin.AdminApplication;

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

}
