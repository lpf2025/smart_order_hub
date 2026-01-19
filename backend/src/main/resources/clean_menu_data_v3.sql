-- 清理权限数据，只保留前端需要的菜单项

-- 删除所有权限数据
DELETE FROM sys_role_perm;
DELETE FROM sys_permission;

-- 重新插入需要的权限数据
INSERT INTO `sys_permission` (`perm_name`, `perm_code`, `parent_id`, `menu_type`) VALUES
-- 系统管理
('系统管理', 'system', 0, 1),
('角色管理', 'system:role', 1, 1),

-- 商家管理
('商家管理', 'merchant', 0, 1),
('商家列表', 'merchant:list', 3, 1),

-- 菜品管理
('菜品管理', 'dish', 0, 1),
('菜品列表', 'dish:list', 5, 1),
('库存管理', 'dish:stock', 5, 1),

-- 订单管理
('订单管理', 'order_menu', 0, 1),
('订单列表', 'order:list', 8, 1);

-- 为超级管理员分配所有权限
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 1, perm_id FROM `sys_permission`;

-- 为普通管理员分配权限（商家管理、菜品管理、订单管理）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 2, perm_id FROM `sys_permission` WHERE perm_code IN ('merchant', 'merchant:list', 'dish', 'dish:list', 'dish:stock', 'order_menu', 'order:list');

-- 为采购员分配权限（菜品管理-库存）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 3, perm_id FROM `sys_permission` WHERE perm_code IN ('dish', 'dish:list', 'dish:stock');

-- 为送餐员分配权限（订单管理）
INSERT INTO `sys_role_perm` (`role_id`, `perm_id`)
SELECT 4, perm_id FROM `sys_permission` WHERE perm_code IN ('order_menu', 'order:list');