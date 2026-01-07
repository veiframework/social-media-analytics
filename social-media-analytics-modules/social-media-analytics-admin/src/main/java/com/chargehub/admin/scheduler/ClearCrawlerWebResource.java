package com.chargehub.admin.scheduler;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Component
public class ClearCrawlerWebResource {

    public void execute(Boolean reload) {
        FileUtil.del(new File(AbstractWorkScheduler.WEB_RESOURCE_PATH));
        DouYinWorkScheduler.clearCache();
        if (BooleanUtils.isTrue(reload)) {
            DouYinWorkScheduler.reloadCache();
        }
    }

}
