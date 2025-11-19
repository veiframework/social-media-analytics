CREATE TABLE `agreement`
(
    `id`          char(32) NOT NULL,
    `type`        int(11)     DEFAULT NULL,
    `content`     text,
    `create_by`   varchar(32) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   varchar(32) DEFAULT NULL COMMENT '更新人',
    `update_time` datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)  DEFAULT '0' COMMENT '删除标志（0-未删除，1-已删除）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='协议管理';


## 协议管理
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4086, '协议管理', 0, 4, 'index4', 'agreement/index', NULL, NULL, 1, 0, 'C', '0', '0', '', 'documentation', '超管',
        '2025-08-08 17:09:20', 'admin', '2025-08-14 15:11:39', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4087, '新增协议', 4086, 0, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'com.chargehub.admin.agreement.domain.Agreement:crud:CREATE', '#', '超管', '2025-08-08 17:41:23', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4088, '编辑协议', 4086, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'com.chargehub.admin.agreement.domain.Agreement:crud:EDIT', '#', '超管', '2025-08-08 17:41:59', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4089, '删除协议', 4086, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'com.chargehub.admin.agreement.domain.Agreement:crud:DELETE', '#', '超管', '2025-08-08 17:42:18', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4090, '协议分页', 4086, 3, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'com.chargehub.admin.agreement.domain.Agreement:crud:PAGE', '#', '超管', '2025-08-08 17:42:54', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4091, '协议详情', 4086, 4, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'com.chargehub.admin.agreement.domain.Agreement:crud:DETAILS', '#', '超管', '2025-08-08 17:43:18', '', NULL, '');

## 字典
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`,
                             `update_time`, `remark`)
VALUES (170, '协议类型', 'agreement_type', '0', '超管', '2025-08-08 16:30:31', '', NULL, NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`,
                             `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`,
                             `update_time`, `remark`)
VALUES (522, 0, '报名须知', '0', 'agreement_type', NULL, 'default', 'N', '0', '超管', '2025-08-08 16:32:20', 'admin',
        '2025-08-08 17:30:21', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`,
                             `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`,
                             `update_time`, `remark`)
VALUES (523, 0, '用户协议', '1', 'agreement_type', NULL, 'default', 'N', '0', '超管', '2025-08-08 16:34:31', 'admin',
        '2025-08-08 17:30:26', NULL);
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`,
                             `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`,
                             `update_time`, `remark`)
VALUES (524, 0, '获取积分', '2', 'agreement_type', NULL, 'default', 'N', '0', '超管', '2025-08-08 17:37:47', 'admin',
        '2025-08-08 17:37:50', NULL);
