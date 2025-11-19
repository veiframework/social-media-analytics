CREATE TABLE `vc_commission_record`
(
    `id`              char(32) NOT NULL,
    `login_id`        char(32)       DEFAULT NULL COMMENT '登录id',
    `source_login_id` char(32)       DEFAULT NULL COMMENT '来源用户id',
    `type`            varchar(32)    DEFAULT NULL COMMENT '类型- commission-提成, withdraw-提现',
    `status`          varchar(32)    DEFAULT NULL COMMENT '状态- received到账, waiting- 即将到账',
    `phone`           varchar(255)   DEFAULT NULL COMMENT '手机号',
    `order_id`        char(32)       DEFAULT NULL COMMENT '订单号',
    `goods_info`      text COMMENT '商品信息',
    `amount`          decimal(10, 2) DEFAULT NULL COMMENT '金额',
    `create_by`       varchar(32)    DEFAULT NULL COMMENT '创建人',
    `create_time`     datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       varchar(32)    DEFAULT NULL COMMENT '更新人',
    `update_time`     datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4139, '提现管理', 0, 7, 'commissionRecord', 'commission/record', NULL, NULL, 1, 0, 'C', '0', '0', '', 'form', '超管',
        '2025-08-28 11:38:25', 'admin', '2025-08-28 11:42:13', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4140, '提现列表', 4139, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'commission:record:page', '#', '超管',
        '2025-08-28 14:17:49', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4141, '审批提现', 4139, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'commission:record:status', '#', '超管',
        '2025-08-28 14:18:07', '', NULL, '');
