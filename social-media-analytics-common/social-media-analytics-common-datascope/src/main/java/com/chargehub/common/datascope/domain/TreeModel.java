package com.chargehub.common.datascope.domain;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhanghaowei
 * @description
 * @date 2023-09-19-13:49
 */
public interface TreeModel<T> {

    /**
     * id
     *
     * @return
     */
    String getId();

    /**
     * 父id
     *
     * @return
     */
    String getParentId();

    /**
     * 设置父级id
     */
    void setParentId(String parentId);

    /**
     * 设置父级id路径
     *
     * @param parentIds
     */
    default void setParentIds(List<String> parentIds) {
    }

    /**
     * 获取父级节点路径
     *
     * @return
     */
    default List<String> getParentIds() {
        return new ArrayList<>();
    }

    /**
     * 树名称
     *
     * @return
     */
    default String getTreeName() {
        return this.getClass().getName();
    }

    /**
     * 树层级
     *
     * @return
     */
    Integer getTreeLevel();

    /**
     * 获取序号
     *
     * @return
     */
    Integer getSequence();

    /**
     * 添加子级
     *
     * @param child
     */
    default void addChildren(List<T> child) {
        List<T> children = getChildren();
        if (CollectionUtils.isEmpty(children)) {
            this.setChildren(Lists.newArrayList());
        }
        this.getChildren().addAll(child);
    }

    /**
     * 获取子级
     *
     * @return
     */
    List<T> getChildren();

    /**
     * 设置子级
     *
     * @param child
     */
    void setChildren(List<T> child);

    /**
     * 转树
     *
     * @param list
     * @param <T>
     * @return
     */
    static <T extends TreeModel<T>> List<T> toTree(List<T> list) {
        return toTree(list, TreeModel::getId, TreeModel::getParentId, TreeModel::addChildren, TreeModel::getSequence, t -> {
        });
    }

    /**
     * 转树
     *
     * @param list
     * @param postProcess
     * @param <T>
     * @return
     */
    static <T extends TreeModel<T>> List<T> toTree(List<T> list, Consumer<T> postProcess) {
        return toTree(list, TreeModel::getId, TreeModel::getParentId, TreeModel::addChildren, TreeModel::getSequence, postProcess);
    }

    /**
     * 转树
     *
     * @param list
     * @param postProcess
     * @param <T>
     * @return
     */
    static <T extends TreeModel<T>> List<T> toTree(List<T> list, Consumer<T> postProcess, Function<T, Integer> seqFun) {
        return toTree(list, TreeModel::getId, TreeModel::getParentId, TreeModel::addChildren, seqFun, postProcess);
    }

    /**
     * 转树
     *
     * @param list
     * @param idFun
     * @param pidFun
     * @param childFc
     * @param seqFun
     * @param postProcess
     * @return
     */
    static <V> List<V> toTree(List<V> list,
                              Function<V, String> idFun,
                              Function<V, String> pidFun,
                              BiConsumer<V, List<V>> childFc,
                              Function<V, Integer> seqFun,
                              Consumer<V> postProcess) {
        List<V> newList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return newList;
        }
        Set<String> leafNodeIds = new HashSet<>();
        Map<String, List<V>> listMap = list.stream().filter(i -> StringUtils.isNotBlank(pidFun.apply(i))).collect(Collectors.groupingBy(pidFun));
        Set<String> idSet = list.stream().map(idFun).collect(Collectors.toSet());
        Deque<V> deque = list.stream().filter(i -> !idSet.contains(pidFun.apply(i))).collect(Collectors.toCollection(LinkedList::new));

        while (!deque.isEmpty()) {
            V node = deque.poll();
            if (node == null) {
                break;
            }
            String id = idFun.apply(node);
            List<V> children = listMap.get(id);
            if (CollectionUtils.isNotEmpty(children)) {
                List<String> collect = children.stream().map(item -> {
                    postProcess.accept(item);
                    return idFun.apply(item);
                }).collect(Collectors.toList());
                leafNodeIds.addAll(collect);
                children.sort(Comparator.comparing(seqFun));
                childFc.accept(node, children);
                children.forEach(deque::push);
            }
            if (!leafNodeIds.contains(id)) {
                postProcess.accept(node);
                newList.add(node);
            }
        }
        newList.sort(Comparator.comparing(seqFun));
        return newList;
    }


    /**
     * 拍平
     *
     * @param list
     * @param <T>
     * @return
     */
    static <T extends TreeModel<T>> List<T> flat(List<T> list) {
        return flat(list, TreeModel::getChildren);
    }


    static <V> List<V> flat(List<V> list, Function<V, List<V>> childFun) {
        List<V> flatList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return flatList;
        }
        Deque<V> deque = new LinkedList<>(list);
        while (!deque.isEmpty()) {
            V node = deque.poll();
            List<V> child = childFun.apply(node);
            flatList.add(node);
            if (CollectionUtils.isEmpty(child)) {
                continue;
            }
            for (int i = child.size() - 1; i >= 0; i--) {
                deque.push(child.get(i));
            }
        }
        return flatList;
    }

}
