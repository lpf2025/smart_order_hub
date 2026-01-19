ALTER TABLE t_merchant ADD COLUMN month_sales INT DEFAULT 0 COMMENT '月销量' AFTER status;
ALTER TABLE t_merchant ADD COLUMN per_capita DECIMAL(10,2) DEFAULT 0.00 COMMENT '人均消费' AFTER month_sales;
ALTER TABLE t_merchant ADD COLUMN rating DECIMAL(3,2) DEFAULT 5.00 COMMENT '评分' AFTER per_capita;
ALTER TABLE t_merchant ADD COLUMN min_order DECIMAL(10,2) DEFAULT 20.00 COMMENT '起送价' AFTER rating;
ALTER TABLE t_merchant ADD COLUMN delivery_fee DECIMAL(10,2) DEFAULT 5.00 COMMENT '配送费' AFTER min_order;
ALTER TABLE t_merchant ADD COLUMN can_reserve INT DEFAULT 1 COMMENT '是否可预订 1-可预订 0-不可预订' AFTER delivery_fee;
