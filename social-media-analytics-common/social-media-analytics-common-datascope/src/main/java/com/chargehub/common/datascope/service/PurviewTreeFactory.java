package com.chargehub.common.datascope.service;


import com.chargehub.common.datascope.domain.PurviewTree;
import com.chargehub.common.datascope.domain.TreeModel;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author zhanghaowei
 * @description
 * @date 2023-09-19-13:02
 */
public interface PurviewTreeFactory {


    /**
     * 数据权限类型描述
     *
     * @return
     */
    String purviewTypeDesc();

    /**
     * 数据权限类型
     *
     * @return
     */
    String purviewType();


    /**
     * 获取权限树
     *
     * @param setChecked
     * @return
     */
    default List<PurviewTree> toTree(Consumer<PurviewTree> setChecked) {
        return TreeModel.toTree(this.getPurviewList(), setChecked);
    }

    /**
     * 获取权限树
     *
     * @param seqFun
     * @param setChecked
     * @return
     */
    default List<PurviewTree> toTree(Consumer<PurviewTree> setChecked, Function<PurviewTree, Integer> seqFun) {
        if (!this.isTree()) {
            List<PurviewTree> collect = this.getPurviewList();
            collect.forEach(setChecked);
            collect.sort(Comparator.comparing(seqFun));
            return postProcess(collect);
        }
        List<PurviewTree> purviewTrees = TreeModel.toTree(this.getPurviewList(), setChecked, seqFun);
        return postProcess(purviewTrees);
    }

    /**
     * 获得平铺数据
     *
     * @return
     */
    List<PurviewTree> getPurviewList();


    /**
     * 转树后处理
     *
     * @param purviewTreeList
     * @return
     */
    default List<PurviewTree> postProcess(List<PurviewTree> purviewTreeList) {
        return purviewTreeList;
    }

    default boolean isTree() {
        return false;
    }

}
