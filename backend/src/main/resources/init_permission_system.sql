-- 权限系统完整初始化脚本

-- 1. 删除现有表
DROP TABLE IF EXISTS `t_admin_role`;
DROP TABLE IF EXISTS `sys_role_perm`;
DROP TABLE IF EXISTS `sys_permission`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `t_admin`;

-- 2. 创建管理员表
CREATE TABLE `t_admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `mobile` VARCHAR(11) NOT NULL COMMENT '手机号',
  `token` VARCHAR(255) NULL COMMENT '登录令牌',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 3. 创建角色表
CREATE TABLE `sys_role` (
  `role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID（主键）',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_desc` VARCHAR(200) NULL COMMENT '角色描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 4. 创建权限表
CREATE TABLE `sys_permission` (
  `perm_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID（主键）',
  `perm_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `perm_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `parent_id` BIGINT NULL COMMENT '父权限ID',
  `menu_type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型（1=菜单，2=按钮）',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`perm_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 5. 创建角色权限关联表
CREATE TABLE `sys_role_perm` (
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

-- 6. 创建管理员角色关联表
CREATE TABLE `t_admin_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `admin_id` BIGINT NOT NULL COMMENT '管理员ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role` (`admin_id`, `role_id`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_admin_role_admin` FOREIGN KEY (`admin_id`) REFERENCES `t_admin` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_admin_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关联表';

-- 7. 插入默认角色数据
INSERT INTO `sys_role` (`role_name`, `role_desc`) VALUES
('超级管理员', '系统全权限，可配置所有商户、角色、菜单权限，查看所有数据'),
('普通管理员', '管理指定商户的菜品、订单、优惠，无角色/权限配置权限'),
('采购员', '仅管理菜品库存、采购相关操作，无配置/订单核销权限'),
('送餐员', '仅查看配送订单、更新配送状态，无任何配置/编辑权限');

-- 8. 插入权限数据
INSERT INTO `sys_permission` (`perm_name`, `perm_code`, `parent_id`, `menu_type`, `sort_order`) VALUES
-- 系统管理
('系统管理', 'system', 0, 1, 1),
('角色管理', 'system:role', 1, 1, 1),
('新增角色', 'system:role:add', 2, 2, 1),
('编辑角色', 'system:role:edit', 2, 2, 2),
('删除角色', 'system:role:delete', 2, 2, 3),
('管理员管理', 'system:admin', 1, 1, 2),
('新增管理员', 'system:admin:add', 7, 2, 1),
('编辑管理员', 'system:admin:edit', 7, 2, 2),
('删除管理员', 'system:admin:delete', 7, 2, 3),
('配置角色', 'system:admin:role', 7, 2, 4),

-- 商家管理
('商家管理', 'merchant', 0, 1, 2),
('商家列表', 'merchant:list', 12, 1, 1),
('查看商家', 'merchant:view', 13, 2, 1),
('新增商家', 'merchant:add', 13, 2, 2),
('编辑商家', 'merchant:edit', 13, 2, 3),
('删除商家', 'merchant:delete', 13, 2, 4),

-- 菜品管理
('菜品管理', 'dish', 0, 1, 3),
('菜品列表', 'dish:list', 18, 1, 1),
('查看菜品', 'dish:view', 19, 2, 1),
('新增菜品', 'dish:add', 19, 2, 2),
('编辑菜品', 'dish:edit', 19, 2, 3),
('删除菜品', 'dish:delete', 19, 2, 4),
('库存管理', 'dish:stock', 18, 1, 2),
('查看库存', 'dish:stock:view', 25, 2, 1),
('更新库存', 'dish:stock:update', 25, 2, 2),

-- 订单管理
('订单管理', 'order_menu', 0, 1, 4),
('订单列表', 'order:list', 27, 1, 1),
('查看订单', 'order:view', 28, 2, 1),
('订单核销', 'order:verify', 28, 2, 2),
('配送状态', 'order:delivery', 27, 1, 2),
('更新配送状态', 'order:delivery:update', 30, 2, 1);

-- 9. 为超级管理员分配所有权限
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 1, perm_id FROM `sys_permission`;

-- 10. 为普通管理员分配权限（商家管理、菜品管理、订单管理）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 2, perm_id FROM `sys_permission` WHERE perm_code IN (
  'merchant', 'merchant:list', 'merchant:view', 'merchant:add', 'merchant:edit', 'merchant:delete',
  'dish', 'dish:list', 'dish:view', 'dish:add', 'dish:edit', 'dish:delete', 'dish:stock', 'dish:stock:view', 'dish:stock:update',
  'order_menu', 'order:list', 'order:view', 'order:verify', 'order:delivery', 'order:delivery:update'
);

-- 11. 为采购员分配权限（菜品管理-库存）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 3, perm_id FROM `sys_permission` WHERE perm_code IN (
  'dish', 'dish:list', 'dish:view', 'dish:stock', 'dish:stock:view', 'dish:stock:update'
);

-- 12. 为送餐员分配权限（订单管理-配送）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 4, perm_id FROM `sys_permission` WHERE perm_code IN (
  'order_menu', 'order:list', 'order:view', 'order:delivery', 'order:delivery:update'
);

-- 13. 插入默认超级管理员账号
INSERT INTO `t_admin` (`username`, `mobile`) VALUES
('超级管理员', '13681978701');

-- 14. 为超级管理员分配角色
INSERT INTO `t_admin_role` (`admin_id`, `role_id`)
SELECT id, 1 FROM `t_admin` WHERE mobile = '13681978701';