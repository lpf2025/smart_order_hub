-- 为 merchant_user 表添加 avatar_url 字段
ALTER TABLE merchant_user ADD COLUMN avatar_url VARCHAR(255) COMMENT '头像URL' AFTER mobile;
