package com.chargehub.common.swagger.config;

import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zhanghaowei
 * @date 2024/07/24 11:34
 */

public class TomcatShutdownConfig {

    private static final Logger log = LoggerFactory.getLogger(TomcatShutdownConfig.class);

    public TomcatShutdownConfig(NacosServiceManager serviceManager) {
        Runtime.getRuntime().addShutdownHook((new Thread(() -> {
            try {
                log.info("下线服务");
                serviceManager.nacosServiceShutDown();
            } catch (NacosException e) {
                log.error(e.getMessage());
            }
        })));
    }

}
