ALTER TABLE t_order ADD COLUMN delivery_user_id BIGINT DEFAULT NULL COMMENT '配送人ID' AFTER merchant_user_id;
