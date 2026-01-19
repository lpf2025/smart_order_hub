-- 创建商家用户表
CREATE TABLE IF NOT EXISTS `merchant_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `wx_openid` varchar(100) NOT NULL COMMENT '微信小程序openid',
  `wx_nickname` varchar(50) DEFAULT NULL COMMENT '微信昵称',
  `mobile` varchar(11) DEFAULT NULL COMMENT '手机号',
  `merchant_id` bigint DEFAULT NULL COMMENT '绑定商户ID',
  `has_delivery_perm` tinyint(1) DEFAULT 0 COMMENT '是否拥有发货权限（0=否，1=是）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wx_openid` (`wx_openid`),
  KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家用户表';
