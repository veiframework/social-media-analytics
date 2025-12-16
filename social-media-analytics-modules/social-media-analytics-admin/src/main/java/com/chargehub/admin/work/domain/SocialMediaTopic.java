package com.chargehub.admin.work.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chargehub.common.security.template.domain.Z9CrudEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhanghaowei
 * @since 1.0
 */
@Data
public class SocialMediaTopic implements Serializable, Z9CrudEntity {
    private static final long serialVersionUID = 5020814167685004996L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String workId;

    private String topic;

    @Override
    public String getUniqueId() {
        return this.id;
    }

    @Override
    public void setUniqueId(String id) {
        this.id = id;
    }
}
