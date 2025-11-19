package com.chargehub.common.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/04/02 13:53
 */
@Mapper
@Repository
public interface ChargeSysDictDataMapper extends BaseMapper<ChargeSysDictData> {

    /**
     * 查询
     *
     * @return
     */
    default LambdaQueryChainWrapper<ChargeSysDictData> lambdaQuery() {
        return new LambdaQueryChainWrapper<>(this);
    }

    /**
     * 更新
     *
     * @return
     */
    default LambdaUpdateChainWrapper<ChargeSysDictData> lambdaUpdate() {
        return new LambdaUpdateChainWrapper<>(this);
    }

    /**
     * 根据字典类型获取
     *
     * @param dictType
     * @return 字典数据集合信息
     */
    default List<ChargeSysDictData> getDictListByType(String dictType) {
        return this.lambdaQuery().eq(ChargeSysDictData::getDictType, dictType).list();
    }


    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType 字典类型
     * @param dictName 字典键值
     * @return 字典标签
     */
    default String getDictValue(String dictType, String dictName) {
        ChargeSysDictData one = this.lambdaQuery().eq(ChargeSysDictData::getDictType, dictType)
                .eq(ChargeSysDictData::getDictLabel, dictName).one();
        if (one == null) {
            return "";
        }
        return one.getDictValue();
    }

    /**
     * 翻译字典名
     *
     * @param dictType
     * @param dictValue
     * @return
     */
    default String getDictName(String dictType, Object dictValue) {
        ChargeSysDictData one = this.lambdaQuery().eq(ChargeSysDictData::getDictType, dictType)
                .eq(ChargeSysDictData::getDictValue, dictValue).one();
        if (one == null) {
            return "";
        }
        return one.getDictLabel();
    }


    /**
     * 获取字典类型下字典值
     * @param dictType
     * @return
     */
    default String getOneDictValue(String dictType) {
        ChargeSysDictData one = this.lambdaQuery().eq(ChargeSysDictData::getDictType, dictType).last("LIMIT 1").one();
        if (one == null) {
            return "";
        }
        return one.getDictValue();
    }

}
