package com.chargehub.thirdparty.api.domain.dto.amap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 13:45
 */
@Data
public class AliMapPicture implements Serializable {
    private static final long serialVersionUID = 1209916693748356428L;

    @JsonProperty("IsCover")
    private Integer isCover;

    @JsonProperty("PicID")
    private String picId;

    @JsonProperty("Url")
    private String url;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("SrcType")
    private String srcType;

}
