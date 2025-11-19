package com.chargehub.influxdb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/10/15 10:19
 */
@Slf4j
public class InfluxDbManager implements DisposableBean, InitializingBean {

    private Map<String, InfluxClient> influxClientMap;

    private final InfluxProperties influxProperties;

    public InfluxDbManager(InfluxProperties influxProperties) {
        this.influxProperties = influxProperties;
    }

    public InfluxClient getClient(String datasource) {
        return this.influxClientMap.get(datasource);
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, InfluxClient> map = new HashMap<>(16);
        Map<String, InfluxProperties.Datasource> datasource = this.influxProperties.getDatasource();
        datasource.forEach((k, v) -> map.put(k, new InfluxClient(v)));
        this.influxClientMap = map;
        log.info("influx client initialized");
    }

    public InfluxProperties getInfluxProperties() {
        return this.influxProperties;
    }

    @Override
    public void destroy() {
        this.influxClientMap.values().forEach(InfluxClient::close);
        log.info("influx client closed");
    }

}
