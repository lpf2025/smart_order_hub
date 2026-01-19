ALTER TABLE merchant_user ADD COLUMN last_login_time DATETIME DEFAULT NULL COMMENT '最近登录时间';
ALTER TABLE merchant_user ADD COLUMN last_logout_time DATETIME DEFAULT NULL COMMENT '最后退出时间';
