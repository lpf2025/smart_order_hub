-- 添加立即配送和预约配送相关字段（可重复执行）

-- 1. 修改商家表 - 添加配送类型配置
-- 检查并添加 takeaway_delivery_types
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_merchant' 
    AND column_name = 'takeaway_delivery_types'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_merchant ADD COLUMN takeaway_delivery_types VARCHAR(50) DEFAULT ''immediate,reserve'' COMMENT ''配送类型配置 immediate-立即配送 reserve-预约配送'' AFTER takeout_time',
    'SELECT ''Column takeaway_delivery_types already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 default_delivery_type
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_merchant' 
    AND column_name = 'default_delivery_type'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_merchant ADD COLUMN default_delivery_type VARCHAR(20) DEFAULT ''immediate'' COMMENT ''默认配送类型 immediate-立即配送 reserve-预约配送'' AFTER takeaway_delivery_types',
    'SELECT ''Column default_delivery_type already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 start_time
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_merchant' 
    AND column_name = 'start_time'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_merchant ADD COLUMN start_time VARCHAR(10) COMMENT ''营业开始时间 HH:mm'' AFTER business_hours',
    'SELECT ''Column start_time already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 end_time
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_merchant' 
    AND column_name = 'end_time'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_merchant ADD COLUMN end_time VARCHAR(10) COMMENT ''营业结束时间 HH:mm'' AFTER start_time',
    'SELECT ''Column end_time already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 修改订单表 - 添加配送类型和预约时间
-- 检查并添加 delivery_type
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_order' 
    AND column_name = 'delivery_type'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_order ADD COLUMN delivery_type VARCHAR(20) DEFAULT ''immediate'' COMMENT ''配送类型 immediate-立即配送 reserve-预约配送'' AFTER order_type',
    'SELECT ''Column delivery_type already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 reserve_time
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_order' 
    AND column_name = 'reserve_time'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_order ADD COLUMN reserve_time DATETIME COMMENT ''预约配送时间'' AFTER delivery_type',
    'SELECT ''Column reserve_time already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 更新现有数据
UPDATE t_merchant SET takeaway_delivery_types = 'immediate,reserve' WHERE takeaway_delivery_types IS NULL OR takeaway_delivery_types = '';
UPDATE t_merchant SET default_delivery_type = 'immediate' WHERE default_delivery_type IS NULL OR default_delivery_type = '';
UPDATE t_order SET delivery_type = 'immediate' WHERE delivery_type IS NULL OR delivery_type = '';
