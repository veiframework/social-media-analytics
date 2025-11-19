package com.chargehub.common.datascope.domain;

import lombok.Data;

import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2024/08/28 16:43
 */
@Data
public class PurviewDataChangeEvent {

    private Set<String> purviewTypes;

    private Set<String> parentIds;

    private Set<String> purviewIds;

    private boolean delete;
}
