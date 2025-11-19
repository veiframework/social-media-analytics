package com.chargehub.thirdparty.api.domain.dto;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/11 14:40
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.dto
 * @Filename：MpTemplateMessageDto
 */
@Data
public class MpTemplateMessageDto {

    private String templateId;
    private String mpAppId;
    private String openId;

    private String first;
    private String keyword1;
    private String keyword2;
    private String keyword3;
    private String keyword4;
    private String keyword5;

    private String keyword1key;
    private String keyword2key;
    private String keyword3key;
    private String keyword4key;
    private String keyword5key;

    private String remark;

    private String pagePath;
    private Integer orderInfoId;
    private String url;
}
