package com.chargehub.admin.work.domain;

import lombok.Data;

import java.util.Collection;

/**
 * @author Zhanghaowei
 * @since 2026-01-10 11:35
 */
@Data
public class SocialMediaWorkCreateTaskEvent {

    private Collection<SocialMediaWork> data;

    public SocialMediaWorkCreateTaskEvent() {
    }

    public SocialMediaWorkCreateTaskEvent(Collection<SocialMediaWork> data) {
        this.data = data;
    }
}
