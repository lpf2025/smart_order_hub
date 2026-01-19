package com.smartorder.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class InitMerchantUserPermissionsTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    public void initPermissions() throws Exception {
        ClassPathResource resource = new ClassPathResource("init_merchant_user_permissions.sql");
        String sql = FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        
        String[] statements = sql.split(";");
        
        for (String statement : statements) {
            statement = statement.trim();
            if (!statement.isEmpty() && !statement.startsWith("--") && !statement.startsWith("SELECT")) {
                try {
                    jdbcTemplate.execute(statement);
                    System.out.println("执行成功: " + statement.substring(0, Math.min(50, statement.length())) + "...");
                } catch (Exception e) {
                    System.err.println("执行失败: " + statement.substring(0, Math.min(50, statement.length())) + "...");
                    System.err.println("错误: " + e.getMessage());
                }
            }
        }
        
        System.out.println("\n=== 验证权限数据 ===");
        System.out.println("所有权限:");
        jdbcTemplate.queryForList("SELECT * FROM sys_permission ORDER BY perm_id").forEach(System.out::println);
        
        System.out.println("\n超级管理员角色:");
        jdbcTemplate.queryForList("SELECT * FROM sys_role WHERE role_name = '超级管理员'").forEach(System.out::println);
        
        System.out.println("\n超级管理员权限关联:");
        jdbcTemplate.queryForList("SELECT rp.*, p.perm_name, p.perm_code FROM sys_role_perm rp LEFT JOIN sys_permission p ON rp.perm_id = p.perm_id WHERE rp.role_id = (SELECT role_id FROM sys_role WHERE role_name = '超级管理员') ORDER BY rp.perm_id").forEach(System.out::println);
    }
}
