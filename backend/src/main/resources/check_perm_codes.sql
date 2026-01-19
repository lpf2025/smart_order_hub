-- 查看当前权限编码
SELECT perm_id, perm_name, perm_code, parent_id, menu_type, sort_order FROM sys_permission WHERE parent_id = 0 ORDER BY sort_order;
