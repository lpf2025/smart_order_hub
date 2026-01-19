package com.smartorder.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class FixMerchantUserPermissionTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    public void fixMerchantUserPermission() {
        try {
            String updateSql = "UPDATE sys_permission SET parent_id = 38 WHERE perm_code = 'system:merchant:user'";
            jdbcTemplate.update(updateSql);
            System.out.println("商家用户管理的parent_id已更新为38");
            
            String checkSql = "SELECT * FROM sys_permission WHERE perm_code = 'system:merchant:user'";
            jdbcTemplate.queryForList(checkSql).forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("修复失败: " + e.getMessage());
        }
    }
    
    @Test
    public void bindMerchantUserPermissionsToSuperAdmin() {
        try {
            Long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM sys_role WHERE role_name = '超级管理员'", Long.class);
            
            Long menuId = jdbcTemplate.queryForObject("SELECT perm_id FROM sys_permission WHERE perm_code = 'system:merchant:user'", Long.class);
            
            String[] rolePerms = {
                "INSERT IGNORE INTO sys_role_perm (role_id, perm_id) VALUES (" + roleId + ", " + menuId + ")",
                "INSERT IGNORE INTO sys_role_perm (role_id, perm_id) VALUES (" + roleId + ", " + (menuId + 1) + ")",
                "INSERT IGNORE INTO sys_role_perm (role_id, perm_id) VALUES (" + roleId + ", " + (menuId + 2) + ")",
                "INSERT IGNORE INTO sys_role_perm (role_id, perm_id) VALUES (" + roleId + ", " + (menuId + 3) + ")"
            };
            
            for (String rolePermSql : rolePerms) {
                jdbcTemplate.update(rolePermSql);
            }
            System.out.println("超级管理员权限绑定成功");
            
            String checkSql = "SELECT rp.*, p.perm_name, p.perm_code FROM sys_role_perm rp LEFT JOIN sys_permission p ON rp.perm_id = p.perm_id WHERE rp.role_id = " + roleId + " AND p.perm_code LIKE 'system:merchant:user%' ORDER BY rp.perm_id";
            jdbcTemplate.queryForList(checkSql).forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("绑定失败: " + e.getMessage());
        }
    }
}
