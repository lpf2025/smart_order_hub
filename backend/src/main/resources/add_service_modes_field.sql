-- 修改商家表 - 支持配送方式多选（可重复执行）

-- 检查并添加 service_modes 字段
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_merchant' 
    AND column_name = 'service_modes'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_merchant ADD COLUMN service_modes VARCHAR(50) DEFAULT ''1,2'' COMMENT ''支持配送方式 1-外卖 2-自取 多个用逗号分隔'' AFTER status',
    'SELECT ''Column service_modes already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有数据：默认支持外卖和自取
UPDATE t_merchant SET service_modes = '1,2' WHERE service_modes IS NULL OR service_modes = '';
