package com.chargehub.admin.datasync.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaSyncStatistic implements Serializable {
    private static final long serialVersionUID = 443507996210332791L;

    private Integer thumbNum;

    private Integer shareNum;

    private Integer likeNum;

    private Integer collectNum;

    private Integer commentNum;

    private Integer playNum;

}
