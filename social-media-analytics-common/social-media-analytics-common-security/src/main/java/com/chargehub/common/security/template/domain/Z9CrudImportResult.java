package com.chargehub.common.security.template.domain;

import lombok.Data;

import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/04V04 16:02
 */
@Data
public class Z9CrudImportResult<V> {


    private static final long serialVersionUID = -8058326489408511234L;

    private List<V> failList;

    private List<V> list;

    public Z9CrudImportResult(List<V> failList, List<V> list) {
        this.failList = failList;
        this.list = list;
    }
}
