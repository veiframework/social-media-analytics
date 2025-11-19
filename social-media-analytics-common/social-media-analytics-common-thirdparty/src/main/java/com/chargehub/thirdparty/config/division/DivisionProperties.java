package com.chargehub.thirdparty.config.division;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "divide")
public class DivisionProperties {

    private String url;

    private String priKey;

    private String payerBankCard;
}
