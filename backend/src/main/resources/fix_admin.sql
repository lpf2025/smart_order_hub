-- 修复管理员账号和角色数据

-- 确保角色存在
INSERT INTO `sys_role` (`role_name`, `role_desc`) VALUES
('超级管理员', '系统全权限，可配置所有商户、角色、菜单权限，查看所有数据'),
('普通管理员', '管理指定商户的菜品、订单、优惠，无角色/权限配置权限'),
('采购员', '仅管理菜品库存、采购相关操作，无配置/订单核销权限'),
('送餐员', '仅查看配送订单、更新配送状态，无任何配置/编辑权限')
ON DUPLICATE KEY UPDATE `role_name` = VALUES(`role_name`);

-- 确保管理员账号存在
INSERT INTO `t_admin` (`username`, `mobile`, `role_id`) VALUES
('超级管理员', '13681978701', 1)
ON DUPLICATE KEY UPDATE `username` = '超级管理员', `role_id` = 1;

-- 检查结果
SELECT a.id, a.username, a.mobile, a.role_id, r.role_name, r.role_desc
FROM t_admin a
LEFT JOIN sys_role r ON a.role_id = r.role_id
WHERE a.mobile = '13681978701';