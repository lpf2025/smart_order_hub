-- 添加立即配送和预约配送相关字段（可重复执行）

-- 创建存储过程：添加字段（如果不存在）
DELIMITER //
DROP PROCEDURE IF EXISTS add_column_if_not_exists //
CREATE PROCEDURE add_column_if_not_exists(
    IN table_name VARCHAR(100),
    IN column_name VARCHAR(100),
    IN column_definition TEXT
)
BEGIN
    DECLARE column_count INT;
    
    SELECT COUNT(*) INTO column_count
    FROM information_schema.columns
    WHERE table_schema = 'food_order_sys'
    AND table_name = table_name
    AND column_name = column_name;
    
    IF column_count = 0 THEN
        SET @sql = CONCAT('ALTER TABLE ', table_name, ' ADD COLUMN ', column_name, ' ', column_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //
DELIMITER ;

-- 执行添加字段
CALL add_column_if_not_exists('t_merchant', 'takeaway_delivery_types', 'VARCHAR(50) DEFAULT ''immediate,reserve'' COMMENT ''配送类型配置 immediate-立即配送 reserve-预约配送'' AFTER takeout_time');
CALL add_column_if_not_exists('t_merchant', 'default_delivery_type', 'VARCHAR(20) DEFAULT ''immediate'' COMMENT ''默认配送类型 immediate-立即配送 reserve-预约配送'' AFTER takeaway_delivery_types');
CALL add_column_if_not_exists('t_merchant', 'start_time', 'VARCHAR(10) COMMENT ''营业开始时间 HH:mm'' AFTER business_hours');
CALL add_column_if_not_exists('t_merchant', 'end_time', 'VARCHAR(10) COMMENT ''营业结束时间 HH:mm'' AFTER start_time');
CALL add_column_if_not_exists('t_order', 'delivery_type', 'VARCHAR(20) DEFAULT ''immediate'' COMMENT ''配送类型 immediate-立即配送 reserve-预约配送'' AFTER order_type');
CALL add_column_if_not_exists('t_order', 'reserve_time', 'DATETIME COMMENT ''预约配送时间'' AFTER delivery_type');

-- 删除存储过程
DROP PROCEDURE IF EXISTS add_column_if_not_exists;

-- 更新现有数据
UPDATE t_merchant SET takeaway_delivery_types = 'immediate,reserve' WHERE takeaway_delivery_types IS NULL OR takeaway_delivery_types = '';
UPDATE t_merchant SET default_delivery_type = 'immediate' WHERE default_delivery_type IS NULL OR default_delivery_type = '';
UPDATE t_order SET delivery_type = 'immediate' WHERE delivery_type IS NULL OR delivery_type = '';
