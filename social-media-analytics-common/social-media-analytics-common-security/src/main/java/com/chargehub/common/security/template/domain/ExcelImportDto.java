package com.chargehub.common.security.template.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Zhanghaowei
 * @date 2024/04/22 14:03
 */
@Data
@Schema
public class ExcelImportDto implements Serializable {
    private static final long serialVersionUID = -321952132273865L;

    @NotBlank
    @Schema(description = "文件地址")
    private String fileUrl;

}
