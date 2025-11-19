CREATE TABLE `vc_member_open_option`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `type`           int(11)        DEFAULT NULL COMMENT '会员开通选项类型 0.连续包月  1.单次月卡  2.单次季卡  3.单次年卡',
    `type_name`      varchar(50)    DEFAULT NULL COMMENT '会员开通选项类型名称',
    `months`         int(11)        DEFAULT NULL COMMENT '当前选项包含月数  -1表示连续无限制',
    `days`           int(11)        DEFAULT NULL COMMENT '当前选项包含天数',
    `original_price` decimal(10, 2) DEFAULT NULL COMMENT '原价（展示使用）',
    `price`          decimal(10, 2) DEFAULT NULL COMMENT '售价',
    `status`         int(11)        DEFAULT NULL COMMENT '选项状态  1上架   0下架',
    `create_by`      varchar(50)    DEFAULT NULL COMMENT '创建人',
    `create_time`    datetime       DEFAULT NULL COMMENT '创建时间',
    `update_by`      varchar(50)    DEFAULT NULL COMMENT '修改人',
    `update_time`    datetime       DEFAULT NULL COMMENT '修改时间',
    `del_flag`       int(1)         DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 230
  DEFAULT CHARSET = utf8mb4;



CREATE TABLE `vc_member_open_record`
(
    `id`               char(32) NOT NULL,
    `user_id`          char(32)       DEFAULT NULL COMMENT '用户id',
    `region_id`        varchar(19)    DEFAULT NULL COMMENT '会员开通时所属城市id',
    `phone`            varchar(255)   DEFAULT NULL COMMENT '手机号',
    `type`             int(11)        DEFAULT NULL COMMENT '会员开通选项类型 0.连续包月  1.单次月卡  2.单次季卡  3.单次年卡',
    `type_name`        varchar(50)    DEFAULT NULL COMMENT '会员开通选项类型名称',
    `months`           int(11)        DEFAULT NULL COMMENT '当前选项包含月数  -1表示连续无限制',
    `days`             int(11)        DEFAULT NULL COMMENT '当前选项包含天数',
    `original_price`   decimal(10, 2) DEFAULT NULL COMMENT '原价（展示使用）',
    `price`            decimal(10, 2) DEFAULT NULL COMMENT '售价',
    `open_time`        datetime       DEFAULT NULL COMMENT '开通时间',
    `status`           int(11)        DEFAULT NULL COMMENT '开通记录状态  0待支付  1已支付 2退款中  3已退款',
    `start_date`       datetime       DEFAULT NULL COMMENT '本次开通记录的开始时间',
    `end_date`         datetime       DEFAULT NULL COMMENT '本次开通记录的结束时间',
    `source`           int(11)        DEFAULT NULL COMMENT '会员来源  1用户自己购买',
    `source_record_id` varchar(50)    DEFAULT NULL COMMENT '会员来源记录id  source=1时记录支付记录id(charg_pay_order_log)',
    `create_by`        varchar(50)    DEFAULT NULL COMMENT '创建人',
    `create_time`      datetime       DEFAULT NULL COMMENT '创建时间',
    `update_by`        varchar(50)    DEFAULT NULL COMMENT '修改人',
    `update_time`      datetime       DEFAULT NULL COMMENT '修改时间',
    `refund_time`      datetime       DEFAULT NULL COMMENT '退款时间',
    `refund_remarks`   varchar(200)   DEFAULT NULL COMMENT '退款备注',
    `refund_no`        varchar(50)    DEFAULT NULL COMMENT '退款订单号',
    `refund_money`     decimal(10, 2) DEFAULT NULL COMMENT '退款金额',
    `active`           int(1)         DEFAULT NULL COMMENT '是否生效中, 0-不生效, 1-生效',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `vc_member_open_option` (`id`, `type`, `type_name`, `months`, `days`, `original_price`, `price`, `status`,
                                     `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES (226, 0, '连续包月', -1, 30, -1.00, 20.00, 1, 'sys', '2024-11-19 18:01:25', NULL, NULL, 1);
INSERT INTO `vc_member_open_option` (`id`, `type`, `type_name`, `months`, `days`, `original_price`, `price`, `status`,
                                     `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES (227, 1, '单次月卡', 1, 30, 29.90, 0.02, 1, 'sys', '2024-11-19 18:01:25', NULL, NULL, 0);
INSERT INTO `vc_member_open_option` (`id`, `type`, `type_name`, `months`, `days`, `original_price`, `price`, `status`,
                                     `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES (228, 2, '单次季卡', 3, 90, 69.00, 23.90, 1, 'sys', '2024-11-19 18:01:25', NULL, NULL, 0);
INSERT INTO `vc_member_open_option` (`id`, `type`, `type_name`, `months`, `days`, `original_price`, `price`, `status`,
                                     `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`)
VALUES (229, 3, '单次年卡', 12, 365, 229.00, 189.90, 1, 'sys', '2024-11-19 18:01:25', '1', '2025-08-28 16:37:15', 0);


## 菜单

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4142, '会员管理', 0, 8, 'memberManagement', NULL, NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'peoples', '超管',
        '2025-08-28 16:26:02', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4143, '开通选项', 4142, 1, 'memberOptions', 'member/openItem', NULL, NULL, 1, 0, 'C', '0', '0', NULL, 'list', '超管',
        '2025-08-28 16:28:40', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4144, '开通记录', 4142, 2, 'memberOpenRecords', 'member/openRecord', NULL, NULL, 1, 0, 'C', '0', '0', '', 'list',
        '超管', '2025-08-28 17:51:41', 'admin', '2025-08-28 17:53:14', '');
