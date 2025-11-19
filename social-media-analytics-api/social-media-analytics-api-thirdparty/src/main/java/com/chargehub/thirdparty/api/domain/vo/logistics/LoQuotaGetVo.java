package com.chargehub.thirdparty.api.domain.vo.logistics;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/1/30 13:49
 * @Project：vc-shoes-server
 * @Package：com.vchaoxi.wx.open.vo.logistics
 * @Filename：LoAccountBindVo
 */
@Data
public class LoQuotaGetVo {

    private Integer errcode;
    private String errmsg;

    /**
     * 电子面单余额
     */
    private Integer quota_num;
}
