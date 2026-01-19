-- 添加用户管理相关的权限数据
-- 用户管理菜单（一级菜单）
INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order) 
VALUES ('用户管理', 'user:list', 0, 1, 5);

-- 获取刚插入的用户管理菜单ID（假设是32）
SET @user_menu_id = LAST_INSERT_ID();

-- 用户列表菜单（二级菜单）
INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order) 
VALUES ('用户列表', 'user:list', @user_menu_id, 1, 1);

-- 获取刚插入的用户列表菜单ID（假设是33）
SET @user_list_id = LAST_INSERT_ID();

-- 用户管理按钮权限
INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order) 
VALUES 
('查看用户', 'user:view', @user_list_id, 2, 1),
('新增用户', 'user:add', @user_list_id, 2, 2),
('编辑用户', 'user:edit', @user_list_id, 2, 3),
('删除用户', 'user:delete', @user_list_id, 2, 4);

-- 查看添加后的权限数据
SELECT perm_id, perm_name, perm_code, parent_id, menu_type, sort_order 
FROM sys_permission 
WHERE perm_id >= @user_menu_id 
ORDER BY perm_id;
