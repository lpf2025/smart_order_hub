-- 初始化商家用户管理和客户管理权限
-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 更新客户管理权限
UPDATE sys_permission SET perm_name = '客户管理', perm_code = 'system:customer' WHERE perm_code = 'user:list';

-- 2. 创建商家用户管理菜单（如果不存在）
INSERT IGNORE INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time)
VALUES ('商家用户管理', 'system:merchant:user', 38, 1, 2, NOW());

-- 3. 获取商家用户管理菜单ID
SET @merchant_user_menu_id = (SELECT perm_id FROM sys_permission WHERE perm_code = 'system:merchant:user');

-- 4. 创建商家用户管理按钮权限
INSERT IGNORE INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time)
VALUES 
('商家用户列表查看', 'system:merchant:user:view', @merchant_user_menu_id, 2, 1, NOW()),
('商家用户编辑', 'system:merchant:user:edit', @merchant_user_menu_id, 2, 2, NOW()),
('发货权限配置', 'system:merchant:user:delivery:perm', @merchant_user_menu_id, 2, 3, NOW());

-- 5. 获取超级管理员角色ID
SET @super_admin_role_id = (SELECT role_id FROM sys_role WHERE role_name = '超级管理员');

-- 6. 为超级管理员绑定客户管理权限
INSERT IGNORE INTO sys_role_perm (role_id, perm_id, create_time)
SELECT @super_admin_role_id, perm_id, NOW() FROM sys_permission WHERE perm_code = 'system:customer';

-- 7. 为超级管理员绑定商家用户管理权限
INSERT IGNORE INTO sys_role_perm (role_id, perm_id, create_time)
SELECT @super_admin_role_id, perm_id, NOW() FROM sys_permission WHERE perm_code LIKE 'system:merchant:user%';

-- 8. 验证权限数据
SELECT '=== 所有权限 ===' AS info;
SELECT * FROM sys_permission ORDER BY perm_id;

SELECT '=== 超级管理员角色 ===' AS info;
SELECT * FROM sys_role WHERE role_name = '超级管理员';

SELECT '=== 超级管理员权限关联 ===' AS info;
SELECT rp.*, p.perm_name, p.perm_code 
FROM sys_role_perm rp 
LEFT JOIN sys_permission p ON rp.perm_id = p.perm_id 
WHERE rp.role_id = @super_admin_role_id 
ORDER BY rp.perm_id;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;
