-- 插入权限数据
INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order) VALUES
('系统管理', 'system', 0, 1, 1),
('角色管理', 'system:role', 1, 1, 1),
('商家管理', 'merchant:list', 0, 1, 2),
('菜品管理', 'dish:list', 0, 1, 3),
('订单管理', 'order:list', 0, 1, 4),
('用户管理', 'user:list', 0, 1, 5);

-- 插入超级管理员角色
INSERT INTO sys_role (role_name, role_desc) VALUES
('超级管理员', '拥有所有权限');

-- 插入超级管理员账号
INSERT INTO t_admin (username, mobile) VALUES
('超级管理员', '13681978701');

-- 为超级管理员分配角色
INSERT INTO t_admin_role (admin_id, role_id) VALUES
(1, 1);

-- 为超级管理员角色分配所有权限
INSERT INTO sys_role_perm (role_id, perm_id)
SELECT 1, perm_id FROM sys_permission;
