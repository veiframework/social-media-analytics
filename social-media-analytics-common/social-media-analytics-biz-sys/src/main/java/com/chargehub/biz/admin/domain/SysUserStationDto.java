package com.chargehub.biz.admin.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2024/12/09 16:59
 */
@Data
public class SysUserStationDto implements Serializable {
    private static final long serialVersionUID = -6168699054465525497L;

    @NotBlank
    private String userId;

    /**
     * 巡检员负责的场站ids
     */
    private Set<String> inspectorStationIds;

    /**
     * 合作商负责的场站ids
     */
    private Set<String> partnerStationIds;
}
