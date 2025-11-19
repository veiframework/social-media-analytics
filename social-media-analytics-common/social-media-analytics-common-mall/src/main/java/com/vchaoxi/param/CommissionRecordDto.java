package com.vchaoxi.param;

import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.vchaoxi.entity.CommissionRecord;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2025/08/27 18:01
 */
@Data
public class CommissionRecordDto implements Serializable, Z9CrudDto<CommissionRecord> {
    private static final long serialVersionUID = -2841073695440834752L;





    private Integer rowNum;

    private String errorMsg;

}
