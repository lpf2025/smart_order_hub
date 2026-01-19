-- 查看当前权限表数据
SELECT perm_id, perm_name, perm_code, parent_id, menu_type 
FROM sys_permission 
ORDER BY perm_id;