CREATE TABLE `wines_receiver_address`
(
    `id`              int(11) NOT NULL AUTO_INCREMENT,
    `user_id`         int(11)      DEFAULT NULL COMMENT '用户id',
    `user_name`       varchar(255) DEFAULT NULL COMMENT '收货人姓名',
    `postal_code`     varchar(255) DEFAULT NULL COMMENT '邮编',
    `province_name`   varchar(255) DEFAULT NULL COMMENT '第一级地址',
    `city_name`       varchar(255) DEFAULT NULL COMMENT '第二级地址',
    `county_name`     varchar(255) DEFAULT NULL COMMENT '第三级地址',
    `street_name`     varchar(255) DEFAULT NULL COMMENT '第四级地址',
    `province_code`   int(11)      DEFAULT NULL,
    `city_code`       int(11)      DEFAULT NULL,
    `county_code`     int(11)      DEFAULT NULL,
    `detail_info`     varchar(255) DEFAULT NULL COMMENT '详细信息',
    `detail_info_new` varchar(255) DEFAULT NULL COMMENT '新选择器详细收货地址信息',
    `tel_number`      varchar(255) DEFAULT NULL COMMENT '收货人手机号码',
    `is_default`      int(11)      DEFAULT '0' COMMENT '是否是默认地址0:否，1:是',
    `insert_time`     datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`       int(11)      DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`     datetime     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 49
  DEFAULT CHARSET = utf8mb4 COMMENT ='收货地址';



CREATE TABLE `wines_goods_specification`
(
    `id`            int(11)      NOT NULL AUTO_INCREMENT,
    `goods_id`      int(11)      NOT NULL DEFAULT '0' COMMENT '商品表的商品ID',
    `specification` varchar(255) NOT NULL DEFAULT '' COMMENT '商品规格名称',
    `value`         varchar(255) NOT NULL DEFAULT '' COMMENT '商品规格值',
    `pic_url`       varchar(255) NOT NULL DEFAULT '' COMMENT '商品规格图片',
    `amount`        decimal(10, 2)        DEFAULT '0.00' COMMENT '商品价格（正常价格）',
    `mem_amount`    decimal(10, 2)        DEFAULT '0.00' COMMENT '商品会员价',
    `vip_amount`    decimal(10, 2)        DEFAULT '0.00' COMMENT '商品vip价格',
    `created_at`    datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`     tinyint(4)            DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 27
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品规格';



CREATE TABLE `wines_car`
(
    `id`               int(11) NOT NULL AUTO_INCREMENT,
    `user_id`          int(11)        DEFAULT NULL COMMENT '用户表的用户ID',
    `goods_id`         int(11)        DEFAULT NULL COMMENT '商品表的商品ID',
    `goods_name`       varchar(127)   DEFAULT NULL COMMENT '商品名称',
    `price`            decimal(10, 2) DEFAULT '0.00' COMMENT '商品货品的价格',
    `number`           smallint(6)    DEFAULT '0' COMMENT '商品货品的数量',
    `checked`          tinyint(4)     DEFAULT '1' COMMENT '购物车中商品是否选择状态',
    `pic_url`          varchar(255)   DEFAULT NULL COMMENT '商品图片或者商品货品图片',
    `specification_id` int(11)        DEFAULT NULL COMMENT '规格的id',
    `specifications`   varchar(1023)  DEFAULT NULL COMMENT '商品规格值列表，采用JSON数组格式',
    `created_at`       datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`       datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`        tinyint(4)     DEFAULT '0' COMMENT '逻辑删除',
    `goods_group`      varchar(255)   DEFAULT NULL COMMENT '分组',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 38
  DEFAULT CHARSET = utf8mb4 COMMENT ='购物车';


CREATE TABLE `wine_base_config`
(
    `id`              int(11) NOT NULL AUTO_INCREMENT,
    `commission_rate` decimal(10, 2) DEFAULT NULL COMMENT '提成比例',
    `insert_time`     datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`     datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`       int(11)        DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`     datetime       DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='提成配置';


CREATE TABLE `vc_user_user`
(
    `id`                  int(11) NOT NULL AUTO_INCREMENT,
    `mp_appid`            varchar(30)  DEFAULT NULL COMMENT '用户所属公众号appid',
    `mp_openid`           varchar(50)  DEFAULT NULL COMMENT '用户公众号openid',
    `ma_appid`            varchar(30)  DEFAULT NULL COMMENT '小程序appId',
    `ma_openid`           varchar(50)  DEFAULT NULL COMMENT '小程序openId',
    `phone`               varchar(20)  DEFAULT NULL COMMENT '用户手机号',
    `openid`              varchar(50)  DEFAULT NULL COMMENT '用户网页应用openid',
    `unionid`             varchar(50)  DEFAULT NULL COMMENT '用户unionid',
    `nickname`            varchar(50)  DEFAULT NULL COMMENT '用户昵称',
    `sex`                 int(11)      DEFAULT NULL COMMENT '普通用户性别  1为男性，2为女性',
    `province`            varchar(30)  DEFAULT NULL COMMENT '普通用户个人资料填写的省份',
    `city`                varchar(30)  DEFAULT NULL COMMENT '普通用户个人资料填写的城市',
    `country`             varchar(50)  DEFAULT NULL COMMENT '国家，如中国为CN',
    `headimg_url`         varchar(255) DEFAULT NULL COMMENT '用户头像',
    `integral`            int(11)      DEFAULT '0' COMMENT '积分',
    `mem_grade`           tinyint(4)   DEFAULT '1' COMMENT '会员等级（1:普通用户 2:会员 3:VIP，酒品售卖中使用）',
    `third_points_access` tinyint(4)   DEFAULT '0' COMMENT '是否有获取邀请的三级用户的积分权限（0:无，1:有） 酒品售卖中使用',
    `insert_time`         datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`         datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`           int(11)      DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`         datetime     DEFAULT NULL COMMENT '删除时间',
    `login_id`            char(32)     DEFAULT NULL COMMENT '登录id',
    `invite_login_id`     char(32)     DEFAULT NULL COMMENT '邀请人登录id',
    `invite_time`         datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '邀请时间',
    `member_expire_date`  datetime     DEFAULT NULL COMMENT '会员到期时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 345
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户相关 - 用户表';


CREATE TABLE `vc_user_pay_record`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `order_no`    varchar(50)    DEFAULT NULL COMMENT '订单单号',
    `user_id`     int(11)        DEFAULT NULL COMMENT '用户id',
    `agent_id`    int(11)        DEFAULT NULL COMMENT '代理商id',
    `shop_id`     int(11)        DEFAULT NULL COMMENT '充值消费的商家id',
    `record_type` int(11)        DEFAULT NULL COMMENT '支付记录类型  1下单',
    `pay_way`     int(11)        DEFAULT NULL COMMENT '支付方式   1微信支付',
    `amount`      decimal(10, 2) DEFAULT NULL COMMENT '支付金额',
    `goods_num`   int(11)        DEFAULT '0' COMMENT '商品数量',
    `status`      int(11)        DEFAULT NULL COMMENT '支付状态  0待支付   1已支付   2已完成   3已退款   4已超时',
    `mch_id`      varchar(20)    DEFAULT NULL COMMENT '微信支付时首付款商户号id',
    `pay_time`    datetime       DEFAULT NULL COMMENT '支付时间',
    `record_id`   int(11)        DEFAULT NULL COMMENT '本条支付记录关联的记录id',
    `insert_time` datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time` datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`   int(11)        DEFAULT '0' COMMENT '逻辑删除',
    `delete_time` datetime       DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 395
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户相关 - 用户支付记录';


CREATE TABLE `vc_shop_wx`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `shop_id`       int(11)      DEFAULT NULL COMMENT '商铺id',
    `mp_appid`      varchar(50)  DEFAULT NULL COMMENT '商家公众号appid',
    `mp_app_secret` varchar(50)  DEFAULT NULL COMMENT '商家公众号appSecret',
    `mp_token`      varchar(50)  DEFAULT NULL COMMENT '公众号token',
    `mp_aes_key`    varchar(50)  DEFAULT NULL COMMENT '公众号aesKey',
    `mp_qr`         varchar(255) DEFAULT NULL COMMENT '商家公众号二维码',
    `mp_main_body`  varchar(50)  DEFAULT NULL COMMENT '商家公众号主体',
    `mch_id`        varchar(20)  DEFAULT NULL COMMENT '商户号',
    `mch_key`       varchar(50)  DEFAULT NULL COMMENT '商户号秘钥',
    `mch_cert_name` varchar(100) DEFAULT NULL COMMENT '商户号证书名称',
    `ma_appid`      varchar(30)  DEFAULT NULL COMMENT '小程序appId',
    `ma_secret`     varchar(50)  DEFAULT NULL COMMENT '小程序秘钥',
    `ma_name`       varchar(50)  DEFAULT NULL COMMENT '小程序名称',
    `ma_qr`         varchar(255) DEFAULT NULL COMMENT '小程序二维码',
    `mp_name`       varchar(30)  DEFAULT NULL COMMENT '公众号名称',
    `custom`        int(11)      DEFAULT '0' COMMENT '是否允许商铺自定义公众号商户信息    1允许  0不允许',
    `custom_edit`   int(11)      DEFAULT '0' COMMENT '当前商家开启独立部署后是否进行修改  1是  0否',
    `insert_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`     int(11)      DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`   datetime     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  DEFAULT CHARSET = utf8mb4 COMMENT ='商户wx配置';


CREATE TABLE `vc_shop_shop`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT,
    `agent_id`       int(11)      DEFAULT NULL COMMENT '代理商id',
    `name`           varchar(255) DEFAULT NULL COMMENT '商家名称',
    `logo`           varchar(255) DEFAULT 'https://static.weicungui.cn/logo/vc_logo.png' COMMENT '商家logo',
    `brand`          varchar(50)  DEFAULT '微存智能柜' COMMENT '商家品牌名称',
    `qr_code`        varchar(255) DEFAULT NULL COMMENT '商家二维码',
    `contacts`       varchar(20)  DEFAULT NULL COMMENT '联系人',
    `business_hours` varchar(100) DEFAULT NULL COMMENT '营业时间',
    `service_phone`  varchar(30)  DEFAULT NULL COMMENT '商家客服电话',
    `promoter_id`    int(11)      DEFAULT '0' COMMENT '客户推广员id',
    `open_logistics` int(1)       DEFAULT '0' COMMENT '是否开启物流',
    `insert_time`    datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`    datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`      int(11)      DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`    datetime     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 122
  DEFAULT CHARSET = utf8mb4 COMMENT ='商户信息';


CREATE TABLE `vc_order_pay_record`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `agent_id`    int(11)        DEFAULT NULL COMMENT '代理商id',
    `shop_id`     int(11)        DEFAULT NULL COMMENT '客户id',
    `order_id`    int(11)        DEFAULT NULL COMMENT '订单id',
    `record_type` int(11)        DEFAULT NULL COMMENT '支付记录类型  1下单',
    `pay_way`     int(11)        DEFAULT NULL COMMENT '支付方式   1微信支付',
    `status`      int(11)        DEFAULT NULL COMMENT '支付状态  0待支付   1已支付   2已完成   3已退款',
    `amount`      decimal(10, 2) DEFAULT NULL COMMENT '支付金额',
    `goods_num`   int(11)        DEFAULT '0' COMMENT '商品数量',
    `remarks`     varchar(255)   DEFAULT NULL COMMENT '备注信息',
    `pay_time`    datetime       DEFAULT NULL COMMENT '支付时间',
    `insert_time` datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time` datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`   int(11)        DEFAULT '0' COMMENT '逻辑删除',
    `delete_time` datetime       DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3547
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单相关 - 订单支付记录表';


CREATE TABLE `vc_order_order`
(
    `id`                  int(11) NOT NULL AUTO_INCREMENT,
    `agent_id`            int(11)        DEFAULT NULL COMMENT '代理商id',
    `shop_id`             int(11)        DEFAULT NULL COMMENT '客户id',
    `order_no`            varchar(255)   DEFAULT NULL COMMENT '订单单号',
    `user_id`             int(11)        DEFAULT NULL COMMENT '用户id',
    `mem_grade`           tinyint(4)     DEFAULT '1' COMMENT '会员等级（1:普通用户 2:会员 3:VIP，酒品售卖中使用）',
    `status`              int(11)        DEFAULT '0' COMMENT '订单状态 0待支付   1已支付（待发货）  2超时未支付 3已发货 4收货完成 5退款操作 6已退款',
    `delivery_status`     int(11)        DEFAULT '0' COMMENT '物流公司的状态 0:默认，1:发货中 3:已发货',
    `amount`              decimal(10, 2) DEFAULT NULL COMMENT '订单总金额',
    `freight`             decimal(10, 2) DEFAULT '0.00' COMMENT '运费',
    `pay_time`            datetime       DEFAULT NULL COMMENT '订单支付时间',
    `pay_way`             int(11)        DEFAULT '1' COMMENT '支付方式1:微信，2:线下',
    `invoice_id`          int(11)        DEFAULT NULL COMMENT '发票的id',
    `tracking_no`         varchar(255)   DEFAULT NULL COMMENT '快递单号',
    `delivery_time`       datetime       DEFAULT NULL COMMENT '发货时间',
    `receiving_time`      datetime       DEFAULT NULL COMMENT '收货时间',
    `receiver_address_id` int(11)        DEFAULT NULL COMMENT '用户收货的id',
    `insert_time`         datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`         datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`           int(11)        DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`         datetime       DEFAULT NULL COMMENT '删除时间',
    `reject_reason`       varchar(255)   DEFAULT NULL COMMENT '驳回原因',
    `picking_logistics`   int(11)        DEFAULT '0' COMMENT '该订单是否进行物流揽件  0无  1有',
    `delivery_logistics`  int(11)        DEFAULT '0' COMMENT '该订单是否进行物流配送  0无  1有',
    `order_type`          int(11)        DEFAULT NULL COMMENT '订单类型  1.柜子订单 2.门店订单 3.预约上门 4.校园订单； 当location_type=0时 该值取值范围为1-3； location_type=1时 该字段取值为4',
    `refund_reason`       varchar(255)   DEFAULT NULL COMMENT '退款原因',
    `pick_time_range`     varchar(255)   DEFAULT NULL COMMENT '取件时间区间',
    `pick_tracking_no`    varchar(255)   DEFAULT NULL COMMENT '取件快递单号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 489
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单相关 - 订单记录';


CREATE TABLE `vc_order_info`
(
    `id`               int(11) NOT NULL AUTO_INCREMENT,
    `agent_id`         int(11)        DEFAULT NULL COMMENT '代理商id',
    `shop_id`          int(11)        DEFAULT NULL COMMENT '客户id',
    `order_id`         int(11)        DEFAULT NULL COMMENT '订单id',
    `order_no`         varchar(255)   DEFAULT NULL COMMENT '订单单号',
    `user_id`          int(11)        DEFAULT NULL COMMENT '用户id',
    `goods_id`         int(11)        DEFAULT NULL COMMENT '商品id',
    `goods_no`         varchar(255)   DEFAULT NULL COMMENT '商品编号',
    `goods_name`       varchar(50)    DEFAULT NULL COMMENT '商品名称',
    `specification_id` int(11)        DEFAULT NULL COMMENT '规格的id',
    `specifications`   varchar(1023)  DEFAULT NULL COMMENT '商品货品的规格列表',
    `user_images`      varchar(2550)  DEFAULT NULL COMMENT '用户上传图片',
    `goods_img`        varchar(255)   DEFAULT NULL COMMENT '商品主图',
    `goods_type_id`    int(11)        DEFAULT NULL COMMENT '商品类型id',
    `goods_type_name`  varchar(50)    DEFAULT NULL COMMENT '商品类型名称',
    `status`           int(11)        DEFAULT '0' COMMENT '订单状态',
    `amount`           decimal(10, 2) DEFAULT NULL COMMENT '订单金额',
    `goods_num`        int(11)        DEFAULT '0' COMMENT '商品数量',
    `unit_price`       decimal(10, 2) DEFAULT '0.00' COMMENT '商品单价',
    `pay_time`         datetime       DEFAULT NULL COMMENT '订单支付时间',
    `insert_time`      datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`      datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`        int(11)        DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`      datetime       DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 544
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单相关 - 订单信息';


CREATE TABLE `vc_goods_type`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `parent_id`   int(11)     DEFAULT '0' COMMENT '父级id',
    `agent_id`    int(11)     DEFAULT '0' COMMENT '代理商id',
    `shop_id`     int(11)     DEFAULT NULL COMMENT '商铺id',
    `name`        varchar(20) DEFAULT NULL COMMENT '类型名称',
    `status`      int(11)     DEFAULT NULL COMMENT '分类状态  1启用  0停用',
    `serial`      int(11)     DEFAULT '1' COMMENT '序号',
    `insert_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time` datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`   int(11)     DEFAULT '0' COMMENT '逻辑删除',
    `delete_time` datetime    DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 337
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品相关 - 商品类型表';


CREATE TABLE `vc_goods_goods`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `agent_id`     int(11)          DEFAULT '0' COMMENT '代理商id',
    `shop_id`      int(11)          DEFAULT NULL COMMENT '商铺id',
    `type`         int(11)          DEFAULT NULL COMMENT '商铺分类',
    `goods_no`     varchar(255)     DEFAULT NULL COMMENT '商品编号',
    `name`         varchar(50)      DEFAULT NULL COMMENT '商品名称',
    `status`       int(11)          DEFAULT NULL COMMENT '分类状态  1上架  0下架',
    `img`          varchar(255)     DEFAULT NULL COMMENT '商品主图',
    `gallery`      varchar(1023)    DEFAULT NULL COMMENT '商品宣传图片列表，采用JSON数组格式',
    `amount`       decimal(10, 2)   DEFAULT NULL COMMENT '商品价格（正常价格）',
    `freight`      decimal(10, 2)   DEFAULT '0.00' COMMENT '运费',
    `mem_amount`   decimal(10, 2)   DEFAULT NULL COMMENT '商品会员价',
    `vip_amount`   decimal(10, 2)   DEFAULT NULL COMMENT '商品vip价格',
    `sales_number` int(11) NOT NULL DEFAULT '0' COMMENT '商品销量',
    `serial`       int(11)          DEFAULT '1' COMMENT '序号',
    `box_sizes`    varchar(20)      DEFAULT NULL COMMENT '商品存放的箱格尺寸类型 多个尺寸英文逗号分割 为空表示支持全部箱格尺寸',
    `unit`         varchar(10)      DEFAULT NULL COMMENT '商品服务单位  双 次 件等',
    `details`      text COMMENT '商品详细介绍，是富文本格式',
    `brief`        varchar(255)     DEFAULT '' COMMENT '商品简介',
    `is_banner`    int(11)          DEFAULT '0' COMMENT '是否设置成了banner（0:未设置，1:已设置）',
    `is_promotion` int(11)          DEFAULT '0' COMMENT '是否促销 0正常  1促销',
    `insert_time`  datetime         DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`  datetime         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`    int(11)          DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`  datetime         DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 664 COMMENT ='商品相关 - 商品表';


CREATE TABLE `vc_agent_wx`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `agent_id`      int(11)      DEFAULT NULL COMMENT '商铺id',
    `mp_appid`      varchar(50)  DEFAULT NULL COMMENT '商家公众号appid',
    `mp_app_secret` varchar(50)  DEFAULT NULL COMMENT '商家公众号appSecret',
    `mp_token`      varchar(50)  DEFAULT NULL COMMENT '公众号token',
    `mp_aes_key`    varchar(50)  DEFAULT NULL COMMENT '公众号aesKey',
    `mp_qr`         varchar(255) DEFAULT NULL COMMENT '商家公众号二维码',
    `mp_main_body`  varchar(50)  DEFAULT NULL COMMENT '商家公众号主体',
    `mch_id`        varchar(20)  DEFAULT NULL COMMENT '商户号',
    `mch_key`       varchar(50)  DEFAULT NULL COMMENT '商户号秘钥',
    `mch_cert_name` varchar(100) DEFAULT NULL COMMENT '商户号证书名称',
    `ma_appid`      varchar(30)  DEFAULT NULL COMMENT '小程序appId',
    `ma_secret`     varchar(50)  DEFAULT NULL COMMENT '小程序秘钥',
    `ma_name`       varchar(50)  DEFAULT NULL COMMENT '小程序名称',
    `ma_qr`         varchar(255) DEFAULT NULL COMMENT '小程序二维码',
    `mp_name`       varchar(30)  DEFAULT NULL COMMENT '公众号名称',
    `custom`        int(11)      DEFAULT '0' COMMENT '是否允许商铺自定义公众号商户信息    1允许  0不允许',
    `insert_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`     int(11)      DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`   datetime     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8mb4 COMMENT ='代理商相关 - 微信配置表';



CREATE TABLE `vc_agent_general_config`
(
    `id`                         int(11) NOT NULL AUTO_INCREMENT,
    `offline_redemption_address` varchar(255) DEFAULT NULL COMMENT '线下核销地址',
    `offline_redemption_contact` varchar(255) DEFAULT NULL COMMENT '线下核销联系人',
    `offline_redemption_phone`   varchar(255) DEFAULT NULL COMMENT '线下核销联系方式',
    `insert_time`                datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`                datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`                  int(11)      DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`                datetime     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


CREATE TABLE `vc_agent_agent`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT,
    `name`          varchar(255) DEFAULT NULL COMMENT '代理商名称',
    `logo`          varchar(255) DEFAULT NULL COMMENT '商家logo',
    `brand`         varchar(50)  DEFAULT NULL COMMENT '品牌名称',
    `service_phone` varchar(50)  DEFAULT NULL COMMENT '客服电话',
    `insert_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `is_delete`     int(11)      DEFAULT '0' COMMENT '逻辑删除',
    `delete_time`   datetime     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb4 COMMENT ='代理商相关 - 代理商表';


# 商品管理菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4092, '商品管理', 0, 5, 'goodsManagement', NULL, NULL, NULL, 1, 0, 'M', '0', '0', '', 'table', '超管',
        '2025-08-11 10:33:58', 'admin', '2025-08-12 15:44:27', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4093, '商品列表', 4092, 1, 'index5', 'goods/index', NULL, NULL, 1, 0, 'C', '0', '0', '', 'build', '超管',
        '2025-08-11 10:35:48', 'admin', '2025-08-12 15:44:48', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4094, '商品分类', 4092, 2, 'type', 'goods/type', NULL, NULL, 1, 0, 'C', '0', '0', '', 'dict', '超管',
        '2025-08-12 11:44:29', 'admin', '2025-08-12 15:45:06', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4108, '订单列表', 4092, 6, 'index2', 'order/index', NULL, NULL, 1, 0, 'C', '0', '0', '', 'shopping', '超管',
        '2025-08-12 15:45:28', 'admin', '2025-08-16 16:03:11', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4095, '添加商品', 4093, 0, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:add:add', '#', '超管',
        '2025-08-12 15:31:39', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4096, '商品列表', 4093, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:list:list', '#', '超管',
        '2025-08-12 15:32:02', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4097, '商品详情', 4093, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:list:info', '#', '超管',
        '2025-08-12 15:32:19', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4098, '设置banner', 4093, 3, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:list:setOrCancel', '#',
        '超管', '2025-08-12 15:32:35', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4099, '设置促销', 4093, 4, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:list:promotion', '#', '超管',
        '2025-08-12 15:32:57', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4100, '商品上下架', 4093, 5, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:list:upAndDown', '#',
        '超管', '2025-08-12 15:33:21', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4101, '编辑商品', 4093, 6, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:list:edit', '#', '超管',
        '2025-08-12 15:33:40', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4102, '删除商品', 4093, 7, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:list:del', '#', '超管',
        '2025-08-12 15:33:56', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4103, '商品分类列表', 4094, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:list', '#', '超管',
        '2025-08-12 15:34:16', 'admin', '2025-08-12 15:34:53', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4104, '添加商品分类', 4094, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:add', '#', '超管',
        '2025-08-12 15:35:07', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4105, '编辑商品分类', 4094, 3, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:edit', '#', '超管',
        '2025-08-12 15:35:28', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4106, '分类上下架', 4094, 4, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:upAndDown', '#',
        '超管', '2025-08-12 15:35:53', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4107, '删除分类', 4094, 5, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:del', '#', '超管',
        '2025-08-12 15:36:10', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4113, '删除订单', 4108, 0, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:order:del', '#', '超管',
        '2025-08-15 14:37:01', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4114, '订单列表', 4108, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:order:list', '#', '超管',
        '2025-08-15 14:37:20', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4115, '订单详情', 4108, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:order:info', '#', '超管',
        '2025-08-15 14:37:43', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4116, '设置物流单号', 4108, 3, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:order:set-tracking-no', '#',
        '超管', '2025-08-15 14:38:14', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4117, '商品分组', 4092, 2, 'type1', 'goods/type1', NULL, NULL, 1, 0, 'C', '0', '0', '', 'dict', '超管',
        '2025-08-22 15:34:55', 'admin', '2025-08-22 15:41:05', '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4118, '商品分类列表', 4117, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:list', '#', '超管',
        '2025-08-22 15:38:58', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4119, '添加商品分类', 4117, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:add', '#', '超管',
        '2025-08-22 15:39:18', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4120, '编辑商品分类', 4117, 3, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:edit', '#', '超管',
        '2025-08-22 15:39:37', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4121, '商品分类上下架', 4117, 4, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:upAndDown', '#',
        '超管', '2025-08-22 15:39:58', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`,
                        `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`,
                        `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4122, '删除分类', 4117, 5, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'operateData:goods:type:del', '#', '超管',
        '2025-08-22 15:40:13', '', NULL, '');


## 支付配置
INSERT INTO `vc_shop_shop` (`id`, `agent_id`, `name`, `logo`, `brand`, `qr_code`, `contacts`, `business_hours`,
                            `service_phone`, `promoter_id`, `insert_time`, `update_time`, `is_delete`, `delete_time`)
VALUES (12, 0, '微存-租赁柜',
        'https://vc-lease-static.oss-cn-zhangjiakou.aliyuncs.com/logo/242672426741106f5e4c8d28f63e2505ba1a25de47_1.jpg',
        '微存租赁柜', NULL, '微存', NULL, '13312341234', 0, '2024-01-06 15:38:54', '2025-08-14 15:22:51', 0, NULL);
INSERT INTO `vc_shop_wx` (`id`, `shop_id`, `mp_appid`, `mp_app_secret`, `mp_token`, `mp_aes_key`, `mp_qr`,
                          `mp_main_body`, `mch_id`, `mch_key`, `mch_cert_name`, `ma_appid`, `ma_secret`, `ma_name`,
                          `ma_qr`, `mp_name`, `custom`, `custom_edit`, `insert_time`, `update_time`, `is_delete`,
                          `delete_time`)
VALUES (1, 12, 'wxb33b804dbe8d870b', NULL, NULL, NULL,
        'https://static.weicungui.cn/logo/wx61480e9318fbc7f8_mp_logo.jpeg', '河南微存智能科技有限公司', '1622344889',
        'vchaoxiPay2022linxuanhaowu770806', '01a676f5-e85c-4116-b959-4c392a4116ad.p12', '', '', '', '', '微存智能柜', 1, 1,
        '2023-06-15 17:36:39', '2025-08-13 18:20:32', 0, NULL);



