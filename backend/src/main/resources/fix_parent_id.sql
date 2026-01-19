-- 修复权限数据的 parent_id

-- 更新角色管理的 parent_id
UPDATE sys_permission SET parent_id = 202 WHERE perm_code = 'system:role';

-- 更新商家列表的 parent_id
UPDATE sys_permission SET parent_id = 204 WHERE perm_code = 'merchant:list';

-- 更新菜品列表和库存管理的 parent_id
UPDATE sys_permission SET parent_id = 206 WHERE perm_code IN ('dish:list', 'dish:stock');

-- 更新订单列表的 parent_id
UPDATE sys_permission SET parent_id = 209 WHERE perm_code = 'order:list';

-- 验证结果
SELECT perm_id, perm_name, perm_code, parent_id, menu_type 
FROM sys_permission 
ORDER BY perm_id;