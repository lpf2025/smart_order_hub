-- 创建角色表（sys_role）

CREATE TABLE IF NOT EXISTS `sys_role` (
  `role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID（主键）',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_desc` VARCHAR(200) NULL COMMENT '角色描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 插入默认角色数据
INSERT INTO `sys_role` (`role_name`, `role_desc`) VALUES
('超级管理员', '系统全权限，可配置所有商户、角色、菜单权限，查看所有数据'),
('普通管理员', '管理指定商户的菜品、订单、优惠，无角色/权限配置权限'),
('采购员', '仅管理菜品库存、采购相关操作，无配置/订单核销权限'),
('送餐员', '仅查看配送订单、更新配送状态，无任何配置/编辑权限');

-- 创建权限表（sys_permission）

CREATE TABLE IF NOT EXISTS `sys_permission` (
  `perm_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID（主键）',
  `perm_name` VARCHAR(50) NOT NULL COMMENT '权限名称（例：新增商家）',
  `perm_code` VARCHAR(100) NOT NULL COMMENT '权限编码（例：merchant:add）',
  `parent_id` BIGINT NULL COMMENT '父权限ID（层级关联）',
  `menu_type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型（1=菜单，2=按钮）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`perm_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 创建角色权限关联表（sys_role_perm）

CREATE TABLE IF NOT EXISTS `sys_role_perm` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `perm_id` BIGINT NOT NULL COMMENT '权限ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`, `perm_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_perm_id` (`perm_id`),
  CONSTRAINT `fk_role_perm_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_perm_perm` FOREIGN KEY (`perm_id`) REFERENCES `sys_permission` (`perm_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';
