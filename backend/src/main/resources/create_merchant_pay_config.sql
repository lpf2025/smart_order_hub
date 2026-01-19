-- 创建商户支付配置表（merchant_pay_config）

CREATE TABLE IF NOT EXISTS `merchant_pay_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` BIGINT NOT NULL COMMENT '关联商户ID（外键）',
  `support_pay_types` VARCHAR(50) NOT NULL DEFAULT 'wechat,alipay' COMMENT '支持的支付方式（例：wechat,alipay）',
  `wechat_config` JSON NULL COMMENT '微信支付配置（商户号、应用ID、密钥等）',
  `alipay_config` JSON NULL COMMENT '支付宝支付配置（PID、应用ID、密钥等）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`),
  CONSTRAINT `fk_merchant_pay_config_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `t_merchant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户支付配置表';
