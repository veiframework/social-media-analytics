package com.chargehub.common.security.template.event;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/07/17 21:36
 */
@Data
public class AfterCreateExportEvent implements Serializable {

    private static final long serialVersionUID = -8069667764218726060L;

    private String id;

    private String businessCode;

    private ObjectNode requestParams;


    public AfterCreateExportEvent(String id, String businessCode, ObjectNode requestParams) {
        this.id = id;
        this.businessCode = businessCode;
        this.requestParams = requestParams;
    }
}
