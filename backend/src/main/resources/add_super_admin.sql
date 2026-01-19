-- 为手机号 13681978701 创建超级管理员账号
INSERT INTO `t_admin` (`username`, `mobile`, `role_id`) VALUES
('超级管理员', '13681978701', 1)
ON DUPLICATE KEY UPDATE `username` = '超级管理员', `role_id` = 1;
