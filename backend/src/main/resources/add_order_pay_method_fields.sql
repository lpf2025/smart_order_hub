-- 添加订单表支付方式和配送单号字段（可重复执行）

-- 检查并添加 pay_method 字段
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_order' 
    AND column_name = 'pay_method'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_order ADD COLUMN pay_method VARCHAR(20) DEFAULT ''wechat'' COMMENT ''支付方式 wechat-微信支付 alipay-支付宝'' AFTER pay_status',
    'SELECT ''Column pay_method already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 delivery_no 字段
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.columns 
    WHERE table_schema = 'food_order_sys' 
    AND table_name = 't_order' 
    AND column_name = 'delivery_no'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE t_order ADD COLUMN delivery_no VARCHAR(50) COMMENT ''配送单号'' AFTER pay_method',
    'SELECT ''Column delivery_no already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有数据
UPDATE t_order SET pay_method = 'wechat' WHERE pay_method IS NULL OR pay_method = '';
