-- 添加数据统计权限
-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 创建数据统计菜单（如果不存在）
INSERT IGNORE INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time)
VALUES ('数据统计', 'statistics:view', 38, 1, 3, NOW());

-- 2. 获取数据统计菜单ID
SET @statistics_menu_id = (SELECT perm_id FROM sys_permission WHERE perm_code = 'statistics:view');

-- 3. 创建数据统计按钮权限
INSERT IGNORE INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time)
VALUES 
('查看数据统计', 'statistics:view', @statistics_menu_id, 2, 1, NOW());

-- 4. 获取超级管理员角色ID
SET @super_admin_role_id = (SELECT role_id FROM sys_role WHERE role_name = '超级管理员');

-- 5. 为超级管理员绑定数据统计权限
INSERT IGNORE INTO sys_role_perm (role_id, perm_id, create_time)
SELECT @super_admin_role_id, perm_id, NOW() FROM sys_permission WHERE perm_code = 'statistics:view';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 查看结果
SELECT * FROM sys_permission WHERE perm_code = 'statistics:view';
