package com.chargehub.common.datascope.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @since 2023-12-14 18:43
 */
@Data
@Schema
@AllArgsConstructor
@NoArgsConstructor
public class Z9Selector implements Serializable {
    private static final long serialVersionUID = 1958069606797240216L;

    @Schema(description = "下拉选项标题")
    private String label;

    @Schema(description = "下拉选项值")
    private String value;

}
