package com.chargehub.common.datascope.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhanghaowei
 * @description
 * @date 2023-09-19-13:04
 */
@Data
public class PurviewTree implements Serializable, TreeModel<PurviewTree> {


    private static final long serialVersionUID = -7545673302104632902L;


    private String id;

    private String parentId;

    private String purviewName;

    private Integer treeLevel;

    private List<PurviewTree> children;

    private Boolean checked;

    private Boolean disabled;

    private boolean unavailable;

    private String remark;

    public PurviewTree() {
        //nothing to do
    }


    @Override
    public Integer getSequence() {
        return 0;
    }


}
