package com.smartorder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class DatabaseCheckTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void checkDatabase() {
        System.out.println("=== 检查管理员账号 ===");
        List<Map<String, Object>> admins = jdbcTemplate.queryForList("SELECT * FROM t_admin");
        for (Map<String, Object> admin : admins) {
            System.out.println(admin);
        }

        System.out.println("\n=== 检查角色 ===");
        List<Map<String, Object>> roles = jdbcTemplate.queryForList("SELECT * FROM sys_role");
        for (Map<String, Object> role : roles) {
            System.out.println(role);
        }

        System.out.println("\n=== 检查权限 ===");
        List<Map<String, Object>> perms = jdbcTemplate.queryForList("SELECT * FROM sys_permission");
        for (Map<String, Object> perm : perms) {
            System.out.println(perm);
        }

        System.out.println("\n=== 检查管理员角色关联 ===");
        List<Map<String, Object>> adminRoles = jdbcTemplate.queryForList("SELECT * FROM t_admin_role");
        for (Map<String, Object> adminRole : adminRoles) {
            System.out.println(adminRole);
        }

        System.out.println("\n=== 检查角色权限关联 ===");
        List<Map<String, Object>> rolePerms = jdbcTemplate.queryForList("SELECT * FROM sys_role_perm");
        for (Map<String, Object> rolePerm : rolePerms) {
            System.out.println(rolePerm);
        }
    }
}
