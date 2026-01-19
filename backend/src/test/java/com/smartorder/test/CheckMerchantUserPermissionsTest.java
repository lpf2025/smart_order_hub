package com.smartorder.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class CheckMerchantUserPermissionsTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    public void checkAllPermissions() {
        System.out.println("=== 检查所有权限 ===");
        jdbcTemplate.queryForList("SELECT * FROM sys_permission ORDER BY perm_id").forEach(System.out::println);
        
        System.out.println("\n=== 检查超级管理员角色 ===");
        jdbcTemplate.queryForList("SELECT * FROM sys_role WHERE role_name = '超级管理员'").forEach(System.out::println);
        
        System.out.println("\n=== 检查角色权限关联 ===");
        jdbcTemplate.queryForList("SELECT rp.*, p.perm_name, p.perm_code FROM sys_role_perm rp LEFT JOIN sys_permission p ON rp.perm_id = p.perm_id WHERE rp.role_id = (SELECT role_id FROM sys_role WHERE role_name = '超级管理员') ORDER BY rp.perm_id").forEach(System.out::println);
        
        System.out.println("\n=== 检查商家用户表 ===");
        jdbcTemplate.queryForList("SELECT * FROM merchant_user").forEach(System.out::println);
    }
}
