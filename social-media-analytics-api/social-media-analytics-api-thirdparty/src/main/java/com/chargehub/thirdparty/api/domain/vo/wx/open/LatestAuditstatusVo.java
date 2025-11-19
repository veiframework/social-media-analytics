package com.chargehub.thirdparty.api.domain.vo.wx.open;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/4/2 15:28
 * @Project：chargehub
 * @Package：com.chargehub.thirdparty.api.domain.vo.wx.open
 * @Filename：LatestAuditstatusVo
 */
@Data
public class LatestAuditstatusVo {

    /**
     * 获取结果
     * true:成功 false:失败
     */
    private Boolean result;

    /**
     * 错误提示
     */
    private String msg;

    /**
     * 最新的审核id
     */
    private Integer auditid;

    /**
     * 审核状态
     * 0审核成功 1审核被拒绝 2审核中 3已撤回 4审核延后
     */
    private Integer status;

    /**
     * 当审核被拒绝时，返回的拒绝原因
     */
    private String reason;

    /**
     * 当审核被拒绝时，会返回审核失败的小程序截图示例。用 竖线I 分隔的 media_id 的列表，可通过获取永久素材接口拉取截图内容
     */
    private String screenshot;

    /**
     * 审核版本
     */
    private String user_version;

    /**
     * 版本描述
     */
    private String user_desc;

    /**
     * 时间戳，提交审核的时间
     */
    private Integer submit_audit_time;
}
