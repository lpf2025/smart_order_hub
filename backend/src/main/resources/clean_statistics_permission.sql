-- 清理重复的数据统计权限
-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 删除重复的权限，保留ID最小的
DELETE FROM sys_role_perm WHERE perm_id IN (50, 51);
DELETE FROM sys_permission WHERE perm_id IN (50, 51);

-- 2. 查看清理后的结果
SELECT * FROM sys_permission WHERE perm_code = 'statistics:view';

-- 3. 查看超级管理员的数据统计权限
SELECT rp.*, p.perm_name, p.perm_code 
FROM sys_role_perm rp
JOIN sys_permission p ON rp.perm_id = p.perm_id
WHERE rp.role_id = (SELECT role_id FROM sys_role WHERE role_name = '超级管理员')
AND p.perm_code = 'statistics:view';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;
