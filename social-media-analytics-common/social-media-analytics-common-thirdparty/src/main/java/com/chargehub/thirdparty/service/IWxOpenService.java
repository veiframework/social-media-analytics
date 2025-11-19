package com.chargehub.thirdparty.service;

import com.chargehub.thirdparty.api.domain.vo.wx.open.LatestAuditstatusVo;
import com.chargehub.thirdparty.api.domain.vo.wx.open.MaVersionInfoVo;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:30
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.service
 * @Filename：IWxOpenService
 */
public interface IWxOpenService {

    /**
     * 获取小程序版本信息
     * @param maAppid
     * @return
     */
    public LatestAuditstatusVo getLatestAuditStatus(String maAppid);


    /**
     * 获取小程序版本信息
     * @param maAppid
     */
    public MaVersionInfoVo getMaVersionInfo(String maAppid);
}
