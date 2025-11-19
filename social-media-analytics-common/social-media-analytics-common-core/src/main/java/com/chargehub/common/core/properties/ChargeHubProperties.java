package com.chargehub.common.core.properties;

import com.chargehub.common.core.utils.ObsBootUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhanghaowei
 * @date 2024/04/02 10:41
 */
@Configuration
@ConfigurationProperties("chargehub")
public class ChargeHubProperties implements InitializingBean {

    private String qrCodeHost;

    @NestedConfigurationProperty
    private HuaweiObs huaweiObs = new HuaweiObs();

    @NestedConfigurationProperty
    private ChargePileProperties pile = new ChargePileProperties();


    private Map<String, RocketProducer> rocket = new HashMap<>();


    public ChargePileProperties getPile() {
        return pile;
    }

    public void setPile(ChargePileProperties pile) {
        this.pile = pile;
    }


    public HuaweiObs getHuaweiObs() {
        return huaweiObs;
    }

    public void setHuaweiObs(HuaweiObs huaweiObs) {
        this.huaweiObs = huaweiObs;
    }

    public String getQrCodeHost() {
        return qrCodeHost;
    }

    public void setQrCodeHost(String qrCodeHost) {
        this.qrCodeHost = qrCodeHost;
    }

    public Map<String, RocketProducer> getRocket() {
        return rocket;
    }

    public void setRocket(Map<String, RocketProducer> rocket) {
        this.rocket = rocket;
    }

    @Override
    public void afterPropertiesSet() {
        ObsBootUtil.setOssClient(huaweiObs);
        ObsBootUtil.setQrCodeHost(qrCodeHost);
    }
}
