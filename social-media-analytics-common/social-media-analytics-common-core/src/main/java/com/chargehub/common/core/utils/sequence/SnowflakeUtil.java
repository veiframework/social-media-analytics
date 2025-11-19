package com.chargehub.common.core.utils.sequence;

import cn.hutool.core.lang.Snowflake;
import com.chargehub.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SnowflakeConfig 雪花Id
 * Date: 2023/08
 *
 * @author TiAmo(13721682347 @ 163.com)
 */
@Slf4j
public class SnowflakeUtil {

    private static final Snowflake snowflake;

    private static InetAddress inetAddress;

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    static {
        long datacenterId = getDatacenterId(31L);
        long workerId = getMaxWorkerId(datacenterId, 31L);
        snowflake = new Snowflake(workerId, datacenterId);
        if (log.isDebugEnabled()) {
            log.debug("[SnowflakeUtil] Initialization Sequence datacenterId={} workerId={}", datacenterId, workerId);
        }
    }


    /**
     * 下一位时间+雪花Id (32位)
     *
     * @return String
     */
    public static String nextTimeId() {
        String format = dateTimeFormatter.format(LocalDateTime.now());
        String string = snowflake.nextIdStr();
        return String.format("%s%s%s", format, string, "0");
    }


    /**
     * nextIdStr
     *
     * @return String
     */
    public static String nextIdStr() {
        return snowflake.nextIdStr();
    }


	@SuppressWarnings("SameParameterValue")
	protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;

        try {
            if (null == inetAddress) {
                inetAddress = InetAddress.getLocalHost();
            }

            NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);
            if (null == network) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = (255L & (long) mac[mac.length - 2] | 65280L & (long) mac[mac.length - 1] << 8) >> 6;
                    id %= maxDatacenterId + 1L;
                }
            }
        } catch (Exception e) {
            log.warn("[SnowflakeUtil] getDatacenterId: {}", e.getMessage());
        }
        return id;
    }


    @SuppressWarnings("SameParameterValue")
    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (StringUtils.isNotBlank(name)) {
            mpid.append(name.split("@")[0]);
        }

        return (long) (mpid.toString().hashCode() & '\uffff') % (maxWorkerId + 1L);
    }

}
