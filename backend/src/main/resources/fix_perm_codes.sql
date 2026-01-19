-- 修复权限编码，使其与前端路径映射匹配
UPDATE sys_permission SET perm_code = 'system:role' WHERE perm_code = 'system:role';
UPDATE sys_permission SET perm_code = 'system:admin' WHERE perm_code = 'system:admin';
UPDATE sys_permission SET perm_code = 'merchant:list' WHERE perm_code = 'merchant';
UPDATE sys_permission SET perm_code = 'dish:list' WHERE perm_code = 'dish';
UPDATE sys_permission SET perm_code = 'order:list' WHERE perm_code = 'order_menu';

-- 查看更新后的结果
SELECT perm_id, perm_name, perm_code, parent_id, menu_type, sort_order FROM sys_permission WHERE parent_id = 0 ORDER BY sort_order;
