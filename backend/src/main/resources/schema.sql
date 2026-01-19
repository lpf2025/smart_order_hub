CREATE DATABASE IF NOT EXISTS food_order_sys DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE food_order_sys;

CREATE TABLE t_merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '门店ID',
    name VARCHAR(100) NOT NULL COMMENT '门店名称',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    address VARCHAR(255) NOT NULL COMMENT '门店地址',
    business_hours VARCHAR(50) COMMENT '营业时间',
    logo_url VARCHAR(255) COMMENT '门店logo',
    status TINYINT DEFAULT 1 COMMENT '状态 1-营业中 2-休息中',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店表';

CREATE TABLE t_dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '菜品ID',
    merchant_id BIGINT NOT NULL COMMENT '门店ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    category VARCHAR(50) NOT NULL COMMENT '菜品分类',
    image_url VARCHAR(255) COMMENT '菜品图片',
    price DECIMAL(10,2) NOT NULL COMMENT '基础价格',
    description TEXT COMMENT '菜品描述',
    stock INT DEFAULT 999 COMMENT '库存数量',
    sales INT DEFAULT 0 COMMENT '销量',
    status TINYINT DEFAULT 1 COMMENT '状态 1-上架 2-下架',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    FOREIGN KEY (merchant_id) REFERENCES t_merchant(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

CREATE TABLE t_dish_spec (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规格ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    spec_name VARCHAR(50) NOT NULL COMMENT '规格名称',
    extra_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '加价',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_dish_id (dish_id),
    FOREIGN KEY (dish_id) REFERENCES t_dish(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品规格表';

CREATE TABLE t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    openid VARCHAR(100) UNIQUE NOT NULL COMMENT '微信openid',
    nickname VARCHAR(100) COMMENT '昵称',
    avatar_url VARCHAR(255) COMMENT '头像',
    phone VARCHAR(20) COMMENT '电话',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_openid (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

CREATE TABLE t_user_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收件人',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收件人电话',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认地址 0-否 1-是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

CREATE TABLE t_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) UNIQUE NOT NULL COMMENT '订单号',
    merchant_id BIGINT NOT NULL COMMENT '门店ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_type TINYINT NOT NULL COMMENT '订单类型 1-堂食 2-外卖',
    table_no VARCHAR(20) COMMENT '桌号（堂食）',
    address_id BIGINT COMMENT '收货地址ID（外卖）',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    status TINYINT DEFAULT 1 COMMENT '订单状态 1-待接单 2-待出餐 3-待配送 4-配送中 5-已完成 6-已取消',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态 0-未支付 1-已支付',
    remark VARCHAR(255) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_order_no (order_no),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (merchant_id) REFERENCES t_merchant(id),
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE t_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    dish_name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    dish_image VARCHAR(255) COMMENT '菜品图片',
    spec_name VARCHAR(50) COMMENT '规格名称',
    spec_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '规格价格',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_order_id (order_id),
    INDEX idx_dish_id (dish_id),
    FOREIGN KEY (order_id) REFERENCES t_order(id),
    FOREIGN KEY (dish_id) REFERENCES t_dish(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项明细表';

CREATE TABLE t_delivery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配送ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    delivery_name VARCHAR(50) COMMENT '配送员姓名',
    delivery_phone VARCHAR(20) COMMENT '配送员电话',
    delivery_status TINYINT DEFAULT 1 COMMENT '配送状态 1-待配送 2-配送中 3-已送达',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_order_id (order_id),
    FOREIGN KEY (order_id) REFERENCES t_order(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送信息表';

CREATE TABLE t_merchant_settlement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结算ID',
    merchant_id BIGINT NOT NULL COMMENT '门店ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    settlement_amount DECIMAL(10,2) NOT NULL COMMENT '结算金额',
    settlement_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '结算时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_order_id (order_id),
    FOREIGN KEY (merchant_id) REFERENCES t_merchant(id),
    FOREIGN KEY (order_id) REFERENCES t_order(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家结算表';

CREATE TABLE t_system_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    operator VARCHAR(50) COMMENT '操作人',
    operation_type VARCHAR(50) COMMENT '操作类型',
    operation_content TEXT COMMENT '操作内容',
    ip VARCHAR(50) COMMENT 'IP地址',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_operator (operator),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';
