import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.chargehub.admin.AdminApplication;
import com.chargehub.admin.work.domain.SocialMediaWork;
import com.chargehub.admin.work.service.SocialMediaWorkService;
import com.chargehub.biz.region.service.impl.RegionInitService;
import com.chargehub.common.core.constant.CacheConstants;
import com.chargehub.common.redis.service.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public void test() {
        regionInitService.addAddList();
    }


    @Autowired
    private RedisService redisService;

    @Test
    public void testCache() {
        redisService.setHashEx("a", "b", "1", 1, TimeUnit.DAYS);
        redisService.setHashEx("a", "c", "1", 1, TimeUnit.DAYS);
        ThreadUtil.safeSleep(5000);
        redisService.deleteCacheMapValue("a", "b");
        ThreadUtil.safeSleep(5000);
        redisService.deleteCacheMapValue("a", "c");
    }


    @Test
    public void testExistKey() {
        boolean existKey = redisService.existKey("work-alarm-last-execute-time:*");
        System.out.println(existKey);
    }

    @Autowired
    private SocialMediaWorkService socialMediaWorkService;

    @Test
    public void setNextCrawTime() {
        List<SocialMediaWork> latestWork0 = socialMediaWorkService.getLatestWork0(null, false);
        LocalDateTime now = LocalDateTime.now();
        for (SocialMediaWork socialMediaWork : latestWork0) {
            String id = socialMediaWork.getId();
            redisService.setCacheMapValue(CacheConstants.WORK_NEXT_CRAWL_TIME, id, now.plusSeconds(30).format(DatePattern.NORM_DATETIME_FORMATTER));
        }

    }

    @Test
    public void testZSet() {
        LocalDateTime now = LocalDateTime.now();
        long timeMillis = LocalDateTimeUtil.toEpochMilli(now);
        Map<String, Double> build = MapUtil.builder(new HashMap<String, Double>())
                .put("2", -2000d + timeMillis)
                .put("0", 0d + timeMillis)
                .put("4", 4000d + timeMillis)
                .put("3", 3000d + timeMillis)
                .put("5", 5000d + timeMillis)
                .build();
        redisService.addZSetMembers("test", build);
        Set<String> filteredMembers = redisService.getZSetMembers("test", true, timeMillis);
        redisService.deleteZSet("test", filteredMembers);
    }

    @Test
    public void testGetZSet() {
        Set<Map<String, Double>> members = redisService.getZSetMembersWithScore(CacheConstants.WORK_NEXT_CRAWL_TIME, 99999);
        for (Map<String, Double> member : members) {
            member.forEach((k, v) -> System.out.println(k + ":" + DateUtil.date(Math.round(v))));
        }
    }

    @Test
    public void testGetZSet2() {
        long timestamp = Long.parseLong(LocalDateTime.now().format(DatePattern.PURE_DATETIME_FORMATTER));
        System.out.println(timestamp);
        Set<String> members = redisService.getZSetMembers(CacheConstants.WORK_NEXT_CRAWL_TIME, true, timestamp);
        for (String member : members) {
            System.out.println(member);
        }
    }


    @Test
    public void testSyncWorks() {
        List<SocialMediaWork> list = socialMediaWorkService.getBaseMapper().lambdaQuery().in(SocialMediaWork::getPlatformId, "douyin", "xiaohongshu").in(SocialMediaWork::getPriority, 1, 2, 3).list();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Double> collect = list.stream().collect(Collectors.toMap(SocialMediaWork::getId, v -> {
            Integer priority = v.getPriority();
            if (priority == 1) {
                return Double.parseDouble(now.plusSeconds(30).format(DatePattern.PURE_DATETIME_FORMATTER));
            }
            if (priority == 2) {
                return Double.parseDouble(now.plusMinutes(30).format(DatePattern.PURE_DATETIME_FORMATTER));
            }
            if (priority == 3) {
                return Double.parseDouble(now.plusHours(24).format(DatePattern.PURE_DATETIME_FORMATTER));
            }
            throw new IllegalArgumentException("can not be here");
        }));
        redisService.addZSetMembers(CacheConstants.WORK_NEXT_CRAWL_TIME, collect);
    }

}
