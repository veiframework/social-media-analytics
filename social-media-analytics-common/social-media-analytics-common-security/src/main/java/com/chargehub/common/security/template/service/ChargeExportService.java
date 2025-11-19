package com.chargehub.common.security.template.service;

import com.chargehub.common.security.template.domain.ChargeExport;

/**
 * @author Zhanghaowei
 * @date 2024/07/16 17:28
 */
public interface ChargeExportService extends Z9CrudService<ChargeExport> {


    void export(String id);

    void termination(String id);

}
