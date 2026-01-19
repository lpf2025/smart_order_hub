-- 新增商家用户管理二级菜单（归属到系统管理，parent_id=1）
INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time) 
VALUES 
('商家用户管理', 'system:merchant:user', 1, 1, 2, NOW())
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- 获取商家用户管理的perm_id
SET @merchant_user_menu_id = LAST_INSERT_ID();

-- 新增商家用户管理按钮权限
INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time) 
VALUES 
('商家用户列表查看', 'system:merchant:user:view', @merchant_user_menu_id, 2, 1, NOW()),
('商家用户编辑', 'system:merchant:user:edit', @merchant_user_menu_id, 2, 2, NOW()),
('发货权限配置', 'system:merchant:user:delivery:perm', @merchant_user_menu_id, 2, 3, NOW())
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- 修改原用户管理为客户管理
UPDATE sys_permission SET perm_name = '客户管理', perm_code = 'system:customer' WHERE perm_code = 'user:list';

-- 获取客户管理的perm_id
SET @customer_menu_id = (SELECT perm_id FROM sys_permission WHERE perm_code = 'system:customer' LIMIT 1);

-- 修改客户管理按钮权限名称（如果存在）
UPDATE sys_permission SET perm_name = '客户列表查看', perm_code = 'system:customer:view' WHERE perm_code LIKE '%user:view%' AND parent_id = @customer_menu_id;
UPDATE sys_permission SET perm_name = '客户信息编辑', perm_code = 'system:customer:edit' WHERE perm_code LIKE '%user:edit%' AND parent_id = @customer_menu_id;
