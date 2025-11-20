package com.chargehub.admin.datasync.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Data
public class SocialMediaWorkResult<T> {


    private List<T> works = new ArrayList<>();

    private boolean hasMore;

    private String nextCursor;

}
