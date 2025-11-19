package com.chargehub.thirdparty.api.domain.dto.divide;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DivideRspDTO {

    private Boolean success;

    private Integer code;

    private String message;

    private String result;

    private Long timestamp;

}
