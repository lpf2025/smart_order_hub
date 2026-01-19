USE food_order_sys;

INSERT INTO t_order (order_no, merchant_id, user_id, order_type, table_no, total_amount, status, pay_status, created_at) VALUES
('ORD20250116001', 1, 1, 1, 'A01', 68.00, 1, 1, NOW() - INTERVAL 2 HOUR),
('ORD20250116002', 1, 1, 1, 'A02', 45.50, 2, 1, NOW() - INTERVAL 1 HOUR),
('ORD20250116003', 1, 1, 2, NULL, 88.00, 3, 1, NOW() - INTERVAL 30 MINUTE),
('ORD20250116004', 1, 1, 1, 'B01', 52.00, 4, 1, NOW() - INTERVAL 15 MINUTE),
('ORD20250116005', 1, 1, 1, 'A03', 35.00, 5, 1, NOW() - INTERVAL 5 MINUTE),
('ORD20250116006', 1, 1, 2, NULL, 76.50, 1, 0, NOW() - INTERVAL 1 MINUTE);

INSERT INTO t_order_item (order_id, dish_id, dish_name, dish_image, spec_name, spec_price, quantity, price, subtotal, created_at) VALUES
(1, 1, '宫保鸡丁', NULL, '标准份', 0.00, 2, 28.00, 56.00, NOW() - INTERVAL 2 HOUR),
(1, 2, '鱼香肉丝', NULL, '标准份', 0.00, 1, 12.00, 12.00, NOW() - INTERVAL 2 HOUR),
(2, 3, '麻婆豆腐', NULL, '标准份', 0.00, 1, 18.00, 18.00, NOW() - INTERVAL 1 HOUR),
(2, 4, '青椒肉丝', NULL, '标准份', 0.00, 1, 15.00, 15.00, NOW() - INTERVAL 1 HOUR),
(2, 5, '西红柿鸡蛋', NULL, '标准份', 0.00, 1, 12.50, 12.50, NOW() - INTERVAL 1 HOUR),
(3, 1, '宫保鸡丁', NULL, '标准份', 0.00, 2, 28.00, 56.00, NOW() - INTERVAL 30 MINUTE),
(3, 6, '红烧肉', NULL, '标准份', 0.00, 1, 32.00, 32.00, NOW() - INTERVAL 30 MINUTE),
(4, 2, '鱼香肉丝', NULL, '标准份', 0.00, 2, 12.00, 24.00, NOW() - INTERVAL 15 MINUTE),
(4, 7, '糖醋里脊', NULL, '标准份', 0.00, 1, 28.00, 28.00, NOW() - INTERVAL 15 MINUTE),
(5, 3, '麻婆豆腐', NULL, '标准份', 0.00, 1, 18.00, 18.00, NOW() - INTERVAL 5 MINUTE),
(5, 8, '酸辣土豆丝', NULL, '标准份', 0.00, 1, 17.00, 17.00, NOW() - INTERVAL 5 MINUTE),
(6, 4, '青椒肉丝', NULL, '标准份', 0.00, 2, 15.00, 30.00, NOW() - INTERVAL 1 MINUTE),
(6, 9, '回锅肉', NULL, '标准份', 0.00, 1, 46.50, 46.50, NOW() - INTERVAL 1 MINUTE);