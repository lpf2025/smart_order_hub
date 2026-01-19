-- 检查管理员账号和角色数据
SELECT a.id, a.username, a.mobile, a.role_id, r.role_name, r.role_desc
FROM t_admin a
LEFT JOIN sys_role r ON a.role_id = r.role_id
WHERE a.mobile = '13681978701';

-- 查看所有角色
SELECT * FROM sys_role;

-- 查看所有管理员
SELECT * FROM t_admin;