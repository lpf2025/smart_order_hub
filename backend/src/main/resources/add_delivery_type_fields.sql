-- 添加立即配送和预约配送相关字段

-- 1. 修改商家表 - 添加配送类型配置
ALTER TABLE t_merchant 
ADD COLUMN takeaway_delivery_types VARCHAR(50) DEFAULT 'immediate,reserve' COMMENT '配送类型配置 immediate-立即配送 reserve-预约配送' AFTER takeout_time,
ADD COLUMN default_delivery_type VARCHAR(20) DEFAULT 'immediate' COMMENT '默认配送类型 immediate-立即配送 reserve-预约配送' AFTER takeaway_delivery_types,
ADD COLUMN start_time VARCHAR(10) COMMENT '营业开始时间 HH:mm' AFTER business_hours,
ADD COLUMN end_time VARCHAR(10) COMMENT '营业结束时间 HH:mm' AFTER start_time;

-- 2. 修改订单表 - 添加配送类型和预约时间
ALTER TABLE t_order 
ADD COLUMN delivery_type VARCHAR(20) DEFAULT 'immediate' COMMENT '配送类型 immediate-立即配送 reserve-预约配送' AFTER order_type,
ADD COLUMN reserve_time DATETIME COMMENT '预约配送时间' AFTER delivery_type;

-- 3. 更新现有数据
UPDATE t_merchant SET takeaway_delivery_types = 'immediate,reserve' WHERE takeaway_delivery_types IS NULL;
UPDATE t_merchant SET default_delivery_type = 'immediate' WHERE default_delivery_type IS NULL;
UPDATE t_order SET delivery_type = 'immediate' WHERE delivery_type IS NULL;
