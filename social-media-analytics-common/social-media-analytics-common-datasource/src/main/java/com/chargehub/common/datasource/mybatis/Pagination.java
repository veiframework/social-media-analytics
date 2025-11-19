package com.chargehub.common.datasource.mybatis;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

/**
 * @author Zhanghaowei
 * @date 2024/04/04 10:51
 */

public class Pagination {

    @ApiModelProperty(value = "当前页码")
    private Long pageNum;

    @ApiModelProperty(value = "每页条数")
    private Long pageSize;

    @ApiModelProperty(hidden = true)
    private long number;

    @ApiModelProperty(hidden = true)
    private long size;

    @ApiModelProperty(value = "升序字段")
    private Set<String> ascFields;

    @ApiModelProperty(value = "降序字段")
    private Set<String> descFields;

    public <T> Page<T> page() {
        long number0 = getNumber();
        long size0 = getSize();
        if (pageNum != null) {
            number0 = getPageNum();
        }
        if (pageSize != null) {
            size0 = getPageSize();
        }
        return new Page<>(number0, size0);
    }


    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Set<String> getAscFields() {
        return ascFields;
    }

    public void setAscFields(Set<String> ascFields) {
        this.ascFields = ascFields;
    }

    public Set<String> getDescFields() {
        return descFields;
    }

    public void setDescFields(Set<String> descFields) {
        this.descFields = descFields;
    }
}
