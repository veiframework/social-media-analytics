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
public class LoAccountGetallVo {

    private Integer errcode;
    private String errmsg;

    /**
     * 账号数量
     */
    private Integer count;

    /**
     * 账号信息列表
     */
    private List<Account> list;

    @Data
    public static class Account {

        /**
         * 快递公司客户编码
         */
        private String 	biz_id;

        /**
         * 快递公司ID
         */
        private String delivery_id;

        /**
         * 账号绑定时间
         */
        private Integer create_time;

        /**
         * 账号更新时间
         */
        private Integer update_time;

        /**
         * 绑定状态
         */
        private Integer status_code;

        /**
         * 账号别名
         */
        private String alias;

        /**
         * 账号绑定失败的错误信息（EMS审核结果）
         */
        private String remark_wrong_msg;

        /**
         * 账号绑定时的备注内容（提交EMS审核需要）
         */
        private String remark_content;

        /**
         * 电子面单余额
         */
        private Integer quota_num;

        /**
         * 电子面单余额更新时间
         */
        private Integer quota_update_time;

        /**
         * 该绑定账号支持的服务类型
         */
        private List<serviceType> service_type;


        
        @Data
        public static class serviceType {

            /**
             * service_type
             */
            private Integer service_type;

            /**
             * 服务类型名称
             */
            private String service_name;
        }
    }
}
