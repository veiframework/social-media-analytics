package com.chargehub.common.datascope.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/04/26 10:12
 */
@Data
@Schema
public class PurviewTypeCheckedVo implements Serializable {

    private static final long serialVersionUID = 7965313337044583801L;


    @Schema(description = "是否选中全部")
    private boolean all;

    @Schema(description = "数据权限类型")
    private String purviewType;

    public PurviewTypeCheckedVo(boolean all, String purviewType) {
        this.all = all;
        this.purviewType = purviewType;
    }
}
