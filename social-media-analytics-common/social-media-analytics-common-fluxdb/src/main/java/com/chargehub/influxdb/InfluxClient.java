package com.chargehub.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.InfluxQLQuery;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.write.WriteParameters;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.InfluxQLQueryResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhanghaowei
 * @date 2024/10/11 14:33
 */
@Slf4j
public class InfluxClient {


    private final InfluxDBClient client;

    private final InfluxProperties.Datasource datasource;

    public InfluxClient(InfluxProperties.Datasource datasource) {
        String bucket = datasource.getBucket();
        String org = datasource.getOrg();
        String token = datasource.getToken();
        String url = datasource.getUrl();
        this.datasource = datasource;
        this.client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }

    public void writeAsync(Object data) {
        WriteApi writeApi = client.makeWriteApi();
        writeApi.writeMeasurement(Stream.of(data).collect(Collectors.toList()), new WriteParameters(datasource.getBucket(), datasource.getOrg(), WritePrecision.S));
    }

    /**
     * 带有@Measurement注解的pojo
     * 写入数据(以秒为时间单位)
     */
    public void write(Object data) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(WritePrecision.S, data);
    }

    /**
     * 写入数据
     * "MyMeasurement,location=河南 temperature=34.2,humidity=99"
     *
     * @param data
     */
    public void write(String data) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeRecord(WritePrecision.S, data);
    }

    /**
     * point写入
     *
     * @param point
     * @param parameters
     */
    public void write(Point point, WriteParameters parameters) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writePoint(point, parameters);
    }

    public Long count(String command) {
        log.debug("influx-sql:\n {}", command);
        List<FluxTable> query = client.getQueryApi().query(command);
        if (CollectionUtils.isEmpty(query)) {
            return 0L;
        }
        FluxTable fluxTable = query.get(0);
        List<FluxRecord> records = fluxTable.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return 0L;
        }
        FluxRecord fluxRecord = records.get(0);
        Object value = fluxRecord.getValue();
        if (value == null) {
            return 0L;
        }
        String result = String.valueOf(value);
        if (StringUtils.isBlank(result)) {
            return 0L;
        }
        return Long.parseLong(result);
    }

    /**
     * 读取数据
     */
    public <M> List<M> queryTable(String command, Class<M> tClass) {
        log.debug("influx-sql:\n {}", command);
        return client.getQueryApi().query(command, tClass);
    }

    public void delete(String column, OffsetDateTime start, OffsetDateTime stop) {
        client.getDeleteApi().delete(start, stop, column, this.datasource.getBucket(), this.datasource.getOrg());
    }

    /**
     * 1.x版本的sql语法
     *
     * @param sql
     * @return
     */
    public List<InfluxQLQueryResult.Result> queryTable(String sql) {
        InfluxQLQuery influxQlQuery = new InfluxQLQuery(sql, datasource.getBucket());
        influxQlQuery.setPrecision(InfluxQLQuery.InfluxQLPrecision.SECONDS);
        InfluxQLQueryResult query = client.getInfluxQLQueryApi().query(influxQlQuery);
        return query.getResults();
    }

    public void close() {
        this.client.close();
    }


}
