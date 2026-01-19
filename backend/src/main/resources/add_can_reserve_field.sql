-- 添加是否支持预约配送字段（可重复执行）

-- 检查并添加 can_reserve 字段
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_merchant' 
    AND column_name = 'can_reserve'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_merchant ADD COLUMN can_reserve TINYINT(1) DEFAULT 1 COMMENT ''是否支持预约配送 0-不支持 1-支持'' AFTER end_time',
    'SELECT ''Column can_reserve already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有数据：默认支持预约配送
UPDATE t_merchant SET can_reserve = 1 WHERE can_reserve IS NULL;
