package com.chargehub.thirdparty.api.domain.vo.wx.open;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:32
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.wx.open
 * @Filename：MaVersionInfoVo
 */
@Data
public class MaVersionInfoVo {

    private Integer errcode;
    private String errmsg;

    private ExpInfo exp_info;
    private ReleaseInfo release_info;


    @Data
    public static class ExpInfo {
        private Integer exp_time;
        private String exp_version;
        private String exp_desc;
    }

    @Data
    public static class ReleaseInfo {
        private Integer release_time;
        private String release_version;
        private String release_desc;
    }
}
