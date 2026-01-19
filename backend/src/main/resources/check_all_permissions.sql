-- 查看所有权限数据
SELECT perm_id, perm_name, perm_code, parent_id, menu_type, sort_order FROM sys_permission ORDER BY parent_id, sort_order;
