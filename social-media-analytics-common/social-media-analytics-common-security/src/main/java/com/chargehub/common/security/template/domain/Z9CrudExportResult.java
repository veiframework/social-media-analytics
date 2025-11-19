package com.chargehub.common.security.template.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/04/07 14:34
 */
@Data
public class Z9CrudExportResult implements Serializable {


    private static final long serialVersionUID = -737006932196345502L;


    private String filename;


    public Z9CrudExportResult() {
    }

    public Z9CrudExportResult(String filename) {
        this.filename = filename;
    }
}
