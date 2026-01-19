-- 为用户表添加角色ID字段
ALTER TABLE `t_user` ADD COLUMN `role_id` BIGINT NULL COMMENT '角色ID' AFTER `phone`;
