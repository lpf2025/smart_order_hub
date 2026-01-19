-- 查看当前权限表数据
SELECT perm_id, perm_name, perm_code, parent_id, menu_type 
FROM sys_permission 
ORDER BY perm_id;

-- 查看角色权限关联
SELECT rp.role_id, r.role_name, rp.perm_id, p.perm_name, p.perm_code, p.parent_id, p.menu_type
FROM sys_role_perm rp
LEFT JOIN sys_role r ON rp.role_id = r.role_id
LEFT JOIN sys_permission p ON rp.perm_id = p.perm_id
ORDER BY rp.role_id, rp.perm_id;