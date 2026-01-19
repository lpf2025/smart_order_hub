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
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_admin_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`role_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 插入默认超级管理员账号（手机号：13800138000）
INSERT INTO `t_admin` (`username`, `mobile`, `role_id`) VALUES
('超级管理员', '13800138000', 1);

-- 插入默认权限数据
INSERT INTO `sys_permission` (`perm_name`, `perm_code`, `parent_id`, `menu_type`) VALUES
-- 系统管理
('系统管理', 'system', 0, 1),
('角色管理', 'system:role', 1, 1),
('角色列表', 'system:role:list', 2, 2),
('新增角色', 'system:role:add', 2, 2),
('编辑角色', 'system:role:edit', 2, 2),
('删除角色', 'system:role:delete', 2, 2),

-- 商家管理
('商家管理', 'merchant', 0, 1),
('商家列表', 'merchant:list', 7, 1),
('查看商家', 'merchant:view', 8, 2),
('新增商家', 'merchant:add', 8, 2),
('编辑商家', 'merchant:edit', 8, 2),
('支付配置', 'merchant:pay', 7, 1),
('查看支付配置', 'merchant:pay:view', 12, 2),
('保存支付配置', 'merchant:pay:save', 12, 2),

-- 菜品管理
('菜品管理', 'dish', 0, 1),
('菜品列表', 'dish:list', 14, 1),
('查看菜品', 'dish:view', 15, 2),
('新增菜品', 'dish:add', 15, 2),
('编辑菜品', 'dish:edit', 15, 2),
('库存管理', 'dish:stock', 14, 1),
('查看库存', 'dish:stock:view', 19, 2),
('更新库存', 'dish:stock:update', 19, 2),

-- 订单管理
('订单管理', 'order', 0, 1),
('订单列表', 'order:list', 21, 1),
('查看订单', 'order:view', 22, 2),
('订单核销', 'order:verify', 22, 2),
('配送状态', 'order:delivery', 21, 1),
('更新配送状态', 'order:delivery:update', 24, 2),

-- 个人中心
('个人中心', 'personal', 0, 1),
('退出登录', 'personal:logout', 26, 2);

-- 为超级管理员分配所有权限
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 1, perm_id FROM `sys_permission`;

-- 为普通管理员分配权限（商家管理、菜品管理、订单管理、个人中心）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`) VALUES
(2, 7), (2, 8), (2, 9), (2, 10), (2, 11),
(2, 12), (2, 13), (2, 14), (2, 15), (2, 16), (2, 17), (2, 18),
(2, 19), (2, 20), (2, 21), (2, 22), (2, 23), (2, 24), (2, 25),
(2, 26), (2, 27);

-- 为采购员分配权限（菜品管理-库存、个人中心）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`) VALUES
(3, 14), (3, 15), (3, 19), (3, 20), (3, 26), (3, 27);

-- 为送餐员分配权限（订单管理-配送、个人中心）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`) VALUES
(4, 21), (4, 22), (4, 24), (4, 25), (4, 26), (4, 27);
