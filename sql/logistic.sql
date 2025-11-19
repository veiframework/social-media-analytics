CREATE TABLE `vc_logistics_account`
(
    `id`                int NOT NULL AUTO_INCREMENT,
    `agent_id`          int          DEFAULT NULL COMMENT '代理商id',
    `shop_id`           int          DEFAULT NULL COMMENT '商家id',
    `delivery_id`       varchar(50)  DEFAULT NULL COMMENT '快递公司编号',
    `delivery_name`     varchar(50)  DEFAULT NULL COMMENT '快递公司名称',
    `biz_id`            varchar(60)  DEFAULT NULL COMMENT '快递公司客户编码',
    `password`          varchar(100) DEFAULT NULL COMMENT '快递公司客户密码',
    `remark_content`    varchar(255) DEFAULT NULL COMMENT '备注内容（提交EMS审核需要） 格式要求： 电话：xxxxx 联系人：xxxxx 服务类型：xxxxx 发货地址：xxxx\n',
    `status`            int          DEFAULT NULL COMMENT '绑定状态  1绑定成功  0解除绑定',
    `quota_num`         int          DEFAULT NULL COMMENT '电子面单余额',
    `quota_update_time` datetime     DEFAULT NULL COMMENT '电子面单余额更新时间',
    `insert_time`       datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`       datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`         int          DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`       datetime     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  DEFAULT CHARSET = utf8mb4;



CREATE TABLE `vc_logistics_address`
(
    `id`            int NOT NULL AUTO_INCREMENT,
    `agent_id`      int          DEFAULT NULL COMMENT '代理商id',
    `shop_id`       int          DEFAULT NULL COMMENT '商家id',
    `name`          varchar(64)  DEFAULT NULL COMMENT '发件人姓名，不超过64字节',
    `tel`           varchar(32)  DEFAULT NULL COMMENT '发件人座机号码，若不填写则必须填写 mobile，不超过32字节',
    `mobile`        varchar(32)  DEFAULT NULL COMMENT '发件人手机号码，若不填写则必须填写 tel，不超过32字节',
    `company`       varchar(64)  DEFAULT NULL COMMENT '发件人公司名称，不超过64字节',
    `post_code`     varchar(10)  DEFAULT NULL COMMENT '发件人邮编，不超过10字节',
    `country`       varchar(64)  DEFAULT NULL COMMENT '发件人国家，不超过64字节',
    `province`      varchar(64)  DEFAULT NULL COMMENT '发件人省份，比如："广东省"，不超过64字节',
    `province_code` int          DEFAULT NULL COMMENT '发件人省份code',
    `city`          varchar(64)  DEFAULT NULL COMMENT '发件人市/地区，比如："广州市"，不超过64字节',
    `city_code`     int          DEFAULT NULL COMMENT '发件人市/地区code',
    `area`          varchar(64)  DEFAULT NULL COMMENT '发件人区/县，比如："海珠区"，不超过64字节',
    `area_code`     int          DEFAULT NULL COMMENT '发件人区/县code',
    `address`       varchar(512) DEFAULT NULL COMMENT '发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节',
    `is_default`    int          DEFAULT NULL COMMENT '是否为默认地址 1是 0否',
    `insert_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`     int          DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`   datetime     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 18
  DEFAULT CHARSET = utf8mb4;



CREATE TABLE `vc_logistics_delivery`
(
    `id`            int NOT NULL AUTO_INCREMENT,
    `delivery_id`   varchar(30)  DEFAULT NULL COMMENT '快递公司 ID',
    `delivery_name` varchar(40)  DEFAULT NULL COMMENT '快递公司名称',
    `can_use_cash`  int          DEFAULT NULL COMMENT '是否支持散单, 1表示支持',
    `can_get_quota` int          DEFAULT NULL COMMENT '是否支持查询面单余额, 1表示支持',
    `service_type`  varchar(500) DEFAULT NULL COMMENT '支持的服务类型',
    `cash_biz_id`   varchar(30)  DEFAULT '' COMMENT '散单对应的bizid，当can_use_cash=1时有效',
    `insert_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`     int          DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`   datetime     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  DEFAULT CHARSET = utf8mb4;



CREATE TABLE `vc_logistics_order`
(
    `id`                     int NOT NULL AUTO_INCREMENT,
    `agent_id`               int            DEFAULT NULL COMMENT '代理商id',
    `shop_id`                int            DEFAULT NULL COMMENT '商家id',
    `order_id`               int            DEFAULT NULL COMMENT '用户订单id',
    `order_no`               varchar(30)    DEFAULT NULL COMMENT '订单单号',
    `order_type`             int            DEFAULT NULL COMMENT '物流订单类型  1揽件订单  2配送订单',
    `wx_order_id`            varchar(30)    DEFAULT NULL COMMENT '唯一订单编号  对应微信端orderId',
    `waybill_id`             varchar(40)    DEFAULT NULL COMMENT '运单号',
    `openid`                 varchar(50)    DEFAULT NULL COMMENT '用户openid，当add_source=2时无需填写（不发送物流服务通知）',
    `delivery_id`            varchar(50)    DEFAULT NULL COMMENT '快递公司ID',
    `delivery_name`          varchar(50)    DEFAULT NULL COMMENT '快递公司名称',
    `biz_id`                 varchar(60)    DEFAULT NULL COMMENT '快递公司客户编码',
    `custom_remark`          varchar(1024)  DEFAULT NULL COMMENT '快递备注信息，比如"易碎物品"，不超过1024字节',
    `tagid`                  int            DEFAULT NULL COMMENT '订单标签id，用于平台型小程序区分平台上的入驻方，tagid须与入驻方账号一一对应，非平台型小程序无需填写该字段',
    `add_source`             int            DEFAULT NULL COMMENT '订单来源，0为小程序订单，2为App或H5订单，填2则不发送物流服务通知',
    `wx_appid`               varchar(50)    DEFAULT NULL COMMENT 'App或H5的appid，add_source=2时必填，需和开通了物流助手的小程序绑定同一open账号',
    `sender_name`            varchar(64)    DEFAULT NULL COMMENT '发件人姓名，不超过64字节',
    `sender_tel`             varchar(32)    DEFAULT NULL COMMENT '发件人座机号码，若不填写则必须填写 mobile，不超过32字节',
    `sender_mobile`          varchar(32)    DEFAULT NULL COMMENT '发件人手机号码，若不填写则必须填写 tel，不超过32字节',
    `sender_company`         varchar(64)    DEFAULT NULL COMMENT '发件人公司名称，不超过64字节',
    `sender_post_code`       varchar(10)    DEFAULT NULL COMMENT '发件人邮编，不超过10字节',
    `sender_country`         varchar(64)    DEFAULT NULL COMMENT '发件人国家，不超过64字节',
    `sender_province`        varchar(64)    DEFAULT NULL COMMENT '发件人省份，比如："广东省"，不超过64字节',
    `sender_province_code`   int            DEFAULT NULL,
    `sender_city`            varchar(64)    DEFAULT NULL COMMENT '发件人市/地区，比如："广州市"，不超过64字节',
    `sender_city_code`       int            DEFAULT NULL,
    `sender_area`            varchar(64)    DEFAULT NULL COMMENT '发件人区/县，比如："海珠区"，不超过64字节',
    `sender_area_code`       int            DEFAULT NULL,
    `sender_address`         varchar(512)   DEFAULT NULL COMMENT '发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节',
    `receiver_name`          varchar(64)    DEFAULT NULL COMMENT '发件人姓名，不超过64字节',
    `receiver_tel`           varchar(32)    DEFAULT NULL COMMENT '发件人座机号码，若不填写则必须填写 mobile，不超过32字节',
    `receiver_mobile`        varchar(32)    DEFAULT NULL COMMENT '发件人手机号码，若不填写则必须填写 tel，不超过32字节',
    `receiver_company`       varchar(64)    DEFAULT NULL COMMENT '发件人公司名称，不超过64字节',
    `receiver_post_code`     varchar(10)    DEFAULT NULL COMMENT '发件人邮编，不超过10字节',
    `receiver_country`       varchar(64)    DEFAULT NULL COMMENT '发件人国家，不超过64字节',
    `receiver_province`      varchar(64)    DEFAULT NULL COMMENT '发件人省份，比如："广东省"，不超过64字节',
    `receiver_province_code` int            DEFAULT NULL,
    `receiver_city`          varchar(64)    DEFAULT NULL COMMENT '发件人市/地区，比如："广州市"，不超过64字节',
    `receiver_city_code`     int            DEFAULT NULL,
    `receiver_area`          varchar(64)    DEFAULT NULL COMMENT '发件人区/县，比如："海珠区"，不超过64字节',
    `receiver_area_code`     int            DEFAULT NULL,
    `receiver_address`       varchar(512)   DEFAULT NULL COMMENT '发件人详细地址，比如："XX路XX号XX大厦XX"，不超过512字节',
    `cargo_count`            int            DEFAULT NULL COMMENT '包裹数量, 默认为1',
    `cargo_weight`           decimal(10, 2) DEFAULT NULL COMMENT '货物总重量，比如1.2，单位是千克(kg)',
    `cargo_space_x`          decimal(10, 2) DEFAULT NULL COMMENT '货物长度，比如20.0，单位是厘米(cm)',
    `cargo_space_y`          decimal(10, 2) DEFAULT NULL COMMENT '货物宽度，比如15.0，单位是厘米(cm)',
    `cargo_space_z`          decimal(10, 2) DEFAULT NULL COMMENT '货物高度，比如10.0，单位是厘米(cm)',
    `cargo_detail_list`      varchar(2000)  DEFAULT NULL COMMENT '物品信息',
    `shop_wxa_path`          varchar(100)   DEFAULT NULL COMMENT '商家小程序的路径，建议为订单页面',
    `shop_img_url`           varchar(255)   DEFAULT NULL COMMENT '商品缩略图 url；shop.detail_list为空则必传，shop.detail_list非空可不传。',
    `shop_goods_name`        varchar(50)    DEFAULT NULL COMMENT '商品名称, 不超过128字节；shop.detail_list为空则必传，shop.detail_list非空可不传。',
    `shop_goods_count`       int            DEFAULT NULL COMMENT '商品数量；shop.detail_list为空则必传。shop.detail_list非空可不传，默认取shop.detail_list的size',
    `shop_detail_list`       varchar(3000)  DEFAULT NULL COMMENT '商品详情列表，适配多商品场景，用以消息落地页展',
    `use_insured`            int            DEFAULT NULL COMMENT '是否保价，0 表示不保价，1 表示保价',
    `insured_value`          int            DEFAULT NULL COMMENT '保价金额，单位是分，比如: 10000 表示 100 元',
    `service_type`           int            DEFAULT NULL COMMENT '服务类型ID',
    `service_name`           varchar(30)    DEFAULT NULL COMMENT '服务名称',
    `expect_time`            int            DEFAULT NULL COMMENT 'Unix 时间戳, 单位秒，顺丰必须传。 预期的上门揽件时间，0表示已事先约定取件时间；否则请传预期揽件时间戳，需大于当前时间，收件员会在预期时间附近上门。例如expect_time为“1557989929”，表示希望收件员将在2019年05月16日14:58:49-15:58:49内上门取货。说明：若选择 了预期揽件时间，请不要自己打单，由上门揽件的时候打印。如果是下顺丰散单，则必传此字段，否则不会有收件员上门揽件。',
    `take_mode`              int            DEFAULT NULL COMMENT '分单策略，【0：线下网点签约，1：总部签约结算】，不传默认线下网点签约。目前支持圆通。',
    `status`                 int            DEFAULT NULL COMMENT '订单状态  1正常  0取消',
    `insert_time`            datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`            datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`              int            DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`            datetime       DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 22
  DEFAULT CHARSET = utf8mb4;


## 快递类型
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (1, 'ANE', '安能物流', 0, 1,
        '[{\"service_type\":95,\"service_name\":\"MINI小包\"},{\"service_type\":22,\"service_name\":\"标准快运\"}]', '',
        '2024-01-31 16:02:12', '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (2, 'BEST', '百世快递', 0, 1, '[{\"service_type\":1,\"service_name\":\"普通快递\"}]', '', '2024-01-31 16:03:45',
        '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (3, 'DB', '德邦快递', 1, 0,
        '[{\"service_type\":1,\"service_name\":\"大件快递3.60\"},{\"service_type\":2,\"service_name\":\"特准快件\"}]',
        'DB_CASH', '2024-01-31 16:04:34', '2024-01-31 16:04:39', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (4, 'EMS', '中国邮政速递物流', 0, 0,
        '[{\"service_type\":6,\"service_name\":\"标准快递\"},{\"service_type\":9,\"service_name\":\"快递包裹\"}]', '',
        '2024-01-31 16:04:52', '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (5, 'HHTT', '天天快递', 0, 1, '[{\"service_type\":0,\"service_name\":\"标准快递\"}]', '', '2024-01-31 16:05:31',
        '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (6, 'JDL', '京东快递', 0, 0, '[{\"service_type\":0,\"service_name\":\"特惠送\"}]', '', '2024-01-31 16:05:59',
        '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (7, 'JTSD', '极兔快递', 0, 1, '[{\"service_type\":0,\"service_name\":\"标准快递\"}]', '', '2024-01-31 16:06:29',
        '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (8, 'PJ', '品骏快递', 0, 0, '[{\"service_type\":1,\"service_name\":\"普通快递\"}]', '', '2024-01-31 16:06:54',
        '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (9, 'SF', '顺丰速运', 1, 0,
        '[{\"service_type\":0,\"service_name\":\"标准快递\"},{\"service_type\":1,\"service_name\":\"顺丰即日\"},{\"service_type\":2,\"service_name\":\"顺丰次晨\"},{\"service_type\":3,\"service_name\":\"顺丰标快\"},{\"service_type\":4,\"service_name\":\"顺丰标快（陆运）\"}]',
        'SF_CASH', '2024-01-31 16:07:31', '2024-01-31 16:07:49', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (10, 'STO', '申通快递', 0, 1, '[{\"service_type\":1,\"service_name\":\"标准快递\"}]', '', '2024-01-31 16:08:00',
        '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (11, 'UCE', '优速快递', 0, 1, '[{\"service_type\":0,\"service_name\":\"大包裹\"}]', '', '2024-01-31 16:08:34',
        '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (12, 'YTO', '圆通速递', 0, 1,
        '[{\"service_type\":0,\"service_name\":\"普通快递\"},{\"service_type\":1,\"service_name\":\"圆准达\"}]', '',
        '2024-01-31 16:09:05', '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (13, 'YUNDA', '韵达快递', 0, 1, '[{\"service_type\":0,\"service_name\":\"标准快件\"}]', '', '2024-01-31 16:09:36',
        '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (14, 'ZTO', '中通快递', 0, 1, '[{\"service_type\":0,\"service_name\":\"标准快件\"}]', '', '2024-01-31 16:10:06',
        '2024-01-31 16:11:27', 0, NULL);
INSERT INTO `vc_logistics_delivery` (`id`, `delivery_id`, `delivery_name`, `can_use_cash`, `can_get_quota`,
                                     `service_type`, `cash_biz_id`, `insert_time`, `update_time`, `is_delete`,
                                     `delete_time`)
VALUES (15, 'TEST', 'TEST', 0, 0, '[{\"service_type\":0,\"service_name\":\"标准快件\"}]', '', '2024-02-01 18:45:05',
        '2024-02-01 18:45:12', 0, NULL);


## 物流菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4123, '物流助手', 0, 6, 'logisticsHelper', NULL, NULL, NULL, 1, 0, 'M', '0', '0', '', 'list', '超管',
        '2025-08-26 16:03:27', 'admin', '2025-08-27 16:52:45', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4124, '物流配置', 4123, 1, 'logistcisAddress', 'logistics/address', NULL, NULL, 1, 0, 'C', '0', '0', '', 'list',
        '超管', '2025-08-26 16:04:25', 'admin', '2025-08-27 16:52:58', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4125, '物流订单', 4123, 2, 'logisticsOrder', 'logistics/order', NULL, NULL, 1, 0, 'C', '0', '0', '', 'list', '超管',
        '2025-08-26 16:04:58', 'admin', '2025-08-27 16:53:04', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4126, '物流地址列表', 4124, 0, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'operateData:logistics:accoAndAddr:addrList', '#', '超管', '2025-08-27 16:54:48', 'admin', '2025-08-27 16:55:02',
        '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4127, '添加物流地址', 4124, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:logistics:accoAndAddr:addrAdd',
        '#', '超管', '2025-08-27 16:55:30', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4128, '修改物流地址', 4124, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'operateData:logistics:accoAndAddr:addrEdit', '#', '超管', '2025-08-27 16:55:56', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4129, '删除物流地址', 4124, 3, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:logistics:accoAndAddr:addrDel',
        '#', '超管', '2025-08-27 16:56:17', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4130, '设置物流状态', 4124, 4, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'operateData:logistics:accoAndAddr:setStatus', '#', '超管', '2025-08-27 16:56:43', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4131, '绑定物流账号', 4124, 5, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'operateData:logistics:accoAndAddr:accoBind', '#', '超管', '2025-08-27 16:57:21', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4132, '解绑物流账号', 4124, 6, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'operateData:logistics:accoAndAddr:accoUnbind', '#', '超管', '2025-08-27 16:57:44', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4133, '物流账号列表', 4124, 7, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0',
        'operateData:logistics:accoAndAddr:accoList', '#', '超管', '2025-08-27 16:58:02', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4134, '创建物流订单', 4125, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:logistics:order:create', '#',
        '超管', '2025-08-27 16:58:42', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4135, '取消物流订单', 4125, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:logistics:order:cancel', '#',
        '超管', '2025-08-27 16:59:03', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4136, '获取物流订单', 4125, 3, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:logistics:order:list', '#',
        '超管', '2025-08-27 16:59:32', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4137, '查看物流轨迹', 4125, 4, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:logistics:order:getPath', '#',
        '超管', '2025-08-27 17:00:13', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4138, '获取面单信息', 4125, 5, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:logistics:order:getOrder', '#',
        '超管', '2025-08-27 17:00:32', '', NULL, '');


