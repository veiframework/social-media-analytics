CREATE TABLE `app_user`
(
    `id`          varchar(32) NOT NULL COMMENT '用户ID',
    `username`    varchar(50)  DEFAULT NULL COMMENT '用户名',
    `nickname`    varchar(50)  DEFAULT NULL COMMENT '昵称',
    `phone`       varchar(11)  DEFAULT NULL COMMENT '手机号',
    `avatar`      varchar(255) DEFAULT NULL COMMENT '头像URL',
    `password`    varchar(100) DEFAULT NULL COMMENT '密码',
    `openid`      varchar(100) DEFAULT NULL COMMENT '微信openid',
    `unionid`     varchar(100) DEFAULT NULL COMMENT '微信unionid',
    `points`      int(11)      DEFAULT '0' COMMENT '积分',
    `create_by`   varchar(32)  DEFAULT NULL COMMENT '创建人',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(32)  DEFAULT NULL COMMENT '更新人',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)   DEFAULT '0' COMMENT '删除标志（0-未删除，1-已删除）',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_username` (`username`) USING BTREE,
    UNIQUE KEY `uk_phone` (`phone`) USING BTREE,
    KEY `idx_openid` (`openid`) USING BTREE,
    KEY `idx_del_flag` (`del_flag`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';


## 用户管理
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4057, '用户管理', 0, 2, 'appuser', 'appuser/index', NULL, NULL, 1, 0, 'C', '0', '0', '', 'peoples', '超管',
        '2025-07-31 14:53:16', 'admin', '2025-07-31 18:06:17', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4058, '用户列表', 4057, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'com.chargehub.admin.user.domain.User:crud:PAGE', '#', '超管', '2025-07-31 15:40:51', 'admin',
        '2025-08-15 14:30:20', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4059, '编辑用户', 4057, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'com.chargehub.admin.user.domain.User:crud:EDIT', '#', '超管', '2025-07-31 15:41:06', 'admin',
        '2025-08-15 14:30:43', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4060, '删除用户', 4057, 3, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'com.chargehub.admin.user.domain.User:crud:DELETE', '#', '超管', '2025-07-31 15:41:21', 'admin',
        '2025-08-15 14:30:55', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4061, '用户详情', 4057, 4, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'com.chargehub.admin.user.domain.User:crud:DETAILS', '#', '超管', '2025-07-31 15:41:33', 'admin',
        '2025-08-15 14:31:30', '');