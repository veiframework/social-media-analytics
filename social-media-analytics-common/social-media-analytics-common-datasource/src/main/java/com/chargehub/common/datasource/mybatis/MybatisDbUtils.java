package com.chargehub.common.datasource.mybatis;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.util.Collection;
import java.util.Optional;

/**
 * @author zhanghaowei
 * @since 1.0
 */
public class MybatisDbUtils {

    private static final Log log = LogFactory.getLog(MybatisDbUtils.class);

    private MybatisDbUtils() {
    }


    public static <T> void saveOrIgnoreBatch(Collection<T> entityList, int batchSize) {
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        Class<T> entityClass = getEntityClass(entityList);
        TableInfo tableInfo = getTableInfo(entityClass);
        Class<?> mapperClass = ClassUtils.toClassConfident(tableInfo.getCurrentNamespace());
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for primary key from entity!");
        SqlHelper.saveOrUpdateBatch(entityClass, mapperClass, log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal) || CollectionUtils.isEmpty(sqlSession.selectList(SqlHelper.getSqlStatement(mapperClass, SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            //ignore save
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getEntityClass(Collection<T> entityList) {
        Class<T> entityClass = null;
        for (T entity : entityList) {
            if (entity != null) {
                entityClass = (Class<T>) entity.getClass();
                break;
            }
        }
        Assert.notNull(entityClass, "error: can not get entityClass from entityList");
        return entityClass;
    }

    public static <T> TableInfo getTableInfo(Class<T> entityClass) {
        return Optional.ofNullable(TableInfoHelper.getTableInfo(entityClass)).orElseThrow(() -> ExceptionUtils.mpe("error: can not find TableInfo from Class: \"%s\".", entityClass.getName()));
    }

}
