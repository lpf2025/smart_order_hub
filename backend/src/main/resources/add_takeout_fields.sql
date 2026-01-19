-- 添加外卖/自取双模式相关字段

-- 1. 修改商家表 - 添加服务模式配置
ALTER TABLE t_merchant 
ADD COLUMN service_mode TINYINT DEFAULT 3 COMMENT '服务模式 1-外卖 2-自取 3-两者都支持' AFTER status,
ADD COLUMN delivery_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送费' AFTER business_hours,
ADD COLUMN min_delivery_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '外卖起送价' AFTER delivery_fee,
ADD COLUMN min_takeout_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '自取起购价' AFTER min_delivery_amount,
ADD COLUMN delivery_time VARCHAR(50) COMMENT '配送时长' AFTER min_takeout_amount,
ADD COLUMN takeout_time VARCHAR(50) COMMENT '取餐时长' AFTER delivery_time;

-- 2. 修改菜品表 - 添加自取价格
ALTER TABLE t_dish 
ADD COLUMN takeout_price DECIMAL(10,2) COMMENT '自取价格' AFTER price;

-- 3. 更新现有数据 - 设置默认值
UPDATE t_merchant SET service_mode = 3 WHERE service_mode IS NULL;
UPDATE t_dish SET takeout_price = price * 0.9 WHERE takeout_price IS NULL;
UPDATE t_merchant SET delivery_fee = 5.00, min_delivery_amount = 20.00, min_takeout_amount = 10.00 WHERE delivery_fee IS NULL;

-- 4. 修改订单表 - 更新order_type注释
ALTER TABLE t_order 
MODIFY COLUMN order_type TINYINT NOT NULL COMMENT '订单类型 1-外卖 2-自取';
