package com.vchaoxi.param;

import com.chargehub.common.security.template.dto.Z9CrudDto;
import com.vchaoxi.entity.MemberOpenRecord;
import lombok.Data;

/**
 * @author Zhanghaowei
 * @date 2025/08/28 17:16
 */
@Data
public class MemberOpenRecordDto implements Z9CrudDto<MemberOpenRecord> {

    private Integer rowNum;

    private String errorMsg;






}
