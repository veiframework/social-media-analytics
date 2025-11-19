package com.chargehub.thirdparty.api.domain.vo.logistics;

import lombok.Data;

import java.util.List;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/1/30 13:49
 * @Project：vc-shoes-server
 * @Package：com.vchaoxi.wx.open.vo.logistics
 * @Filename：LoAccountBindVo
 */
@Data
public class LoPathGetVo {

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 快递公司 ID
     */
    private String delivery_id;

    /**
     * 运单 ID
     */
    private String waybill_id;

    /**
     * 轨迹节点数量
     */
    private Integer path_item_num;

    /**
     * 轨迹节点列表
     */
    private List<PathItem> path_item_list;

    @Data
    public static class PathItem {

        /**
         * 轨迹节点 Unix 时间戳
         */
        private Integer action_time;

        /**
         * 轨迹节点类型
         *
         * 100001	揽件阶段-揽件成功
         * 100002	揽件阶段-揽件失败
         * 100003	揽件阶段-分配业务员
         * 200001	运输阶段-更新运输轨迹
         * 300002	派送阶段-开始派送
         * 300003	派送阶段-签收成功
         * 300004	派送阶段-签收失败
         * 400001	异常阶段-订单取消
         * 400002	异常阶段-订单滞留
         */
        private Integer action_type;

        /**
         * 轨迹节点详情
         */
        private String action_msg;
    }
}
