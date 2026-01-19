-- 清理重复的角色和权限数据

-- 1. 删除重复的角色数据，只保留前4个
DELETE FROM sys_role WHERE role_id > 4;

-- 2. 删除重复的角色权限关联数据，只保留前31条
DELETE FROM sys_role_perm WHERE id > 31;

-- 3. 修正管理员手机号
UPDATE t_admin SET mobile = '13681978701' WHERE id = 1;

-- 4. 验证数据
SELECT '管理员数据' as '表名', COUNT(*) as '记录数' FROM t_admin
UNION ALL
SELECT '角色数据', COUNT(*) FROM sys_role
UNION ALL
SELECT '权限数据', COUNT(*) FROM sys_permission
UNION ALL
SELECT '管理员角色关联', COUNT(*) FROM t_admin_role
UNION ALL
SELECT '角色权限关联', COUNT(*) FROM sys_role_perm;