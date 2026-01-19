ALTER TABLE t_order ADD COLUMN merchant_user_id BIGINT DEFAULT NULL COMMENT '接单商户用户ID' AFTER merchant_id;
ALTER TABLE t_delivery ADD COLUMN merchant_user_id BIGINT DEFAULT NULL COMMENT '配送商户用户ID' AFTER order_id;
