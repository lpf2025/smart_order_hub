-- 创建管理员表（t_admin）
CREATE TABLE IF NOT EXISTS `t_admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NULL COMMENT '密码',
  `mobile` VARCHAR(11) NOT NULL COMMENT '手机号',
  `role_id` BIGINT NULL COMMENT '角色ID',
  `token` VARCHAR(255) NULL COMMENT '登录令牌',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mobile` (`mobile`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入默认超级管理员账号（手机号：13800138000）
INSERT INTO `t_admin` (`username`, `mobile`, `role_id`) VALUES
('超级管理员', '13800138000', 1)
ON DUPLICATE KEY UPDATE `username` = '超级管理员';
