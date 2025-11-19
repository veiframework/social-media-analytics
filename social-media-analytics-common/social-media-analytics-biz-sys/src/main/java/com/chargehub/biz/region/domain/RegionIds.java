package com.chargehub.biz.region.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import lombok.Data;

/**
 * @author Zhanghaowei
 * @date 2024/09/01 09:27
 */
@Data
@TableName("charge_region_ids")
public class RegionIds implements Z9CrudEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String regionId;

    private String parentId;

    @Override
    public String getUniqueId() {
        return id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}
