package com.chargehub.common.security.mapper;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.chargehub.common.security.utils.AppSecurityUtil;
import com.chargehub.common.security.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @author Zhanghaowei
 * @date 2024/04/08 19:13
 */
public class DefaultMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        Object idObj = SecurityUtils.getUserId();
        if (idObj == null) {
            idObj = AppSecurityUtil.userId();
        }
        if (idObj != null) {
            String id = String.valueOf(idObj);
            this.strictInsertFill(metaObject, "creator", String.class, id);
            this.strictInsertFill(metaObject, "updater", String.class, id);
            this.strictInsertFill(metaObject, "createBy", String.class, id);
            this.strictInsertFill(metaObject, "updateBy", String.class, id);
        }
        final Date date = new Date();
        this.strictInsertFill(metaObject, "createTime", Date.class, date);
        this.strictInsertFill(metaObject, "insertTime", Date.class, date);
        this.strictInsertFill(metaObject, "updateTime", Date.class, date);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object idObj = SecurityUtils.getUserId();
        if (idObj == null) {
            idObj = AppSecurityUtil.userId();
        }
        if (idObj != null) {
            String id = String.valueOf(idObj);
            this.strictUpdateFill(metaObject, "updater", String.class, id);
            this.strictUpdateFill(metaObject, "updateBy", String.class, id);
        }
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
