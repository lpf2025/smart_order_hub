package com.smartorder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class FixRolePermTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void fixRolePerm() {
        System.out.println("=== 修复角色权限关联 ===");

        try {
            System.out.println("1. 获取管理员ID...");
            Integer adminId = jdbcTemplate.queryForObject("SELECT id FROM t_admin WHERE mobile = '13681978701'", Integer.class);
            System.out.println("管理员ID: " + adminId);

            System.out.println("2. 获取角色ID...");
            Integer roleId = jdbcTemplate.queryForObject("SELECT role_id FROM sys_role WHERE role_name = '超级管理员'", Integer.class);
            System.out.println("角色ID: " + roleId);

            System.out.println("3. 为管理员分配角色...");
            jdbcTemplate.execute("DELETE FROM t_admin_role WHERE admin_id = " + adminId);
            jdbcTemplate.execute("INSERT INTO t_admin_role (admin_id, role_id) VALUES (" + adminId + ", " + roleId + ")");
            System.out.println("角色分配成功");

            System.out.println("4. 为角色分配所有权限...");
            jdbcTemplate.execute("DELETE FROM sys_role_perm WHERE role_id = " + roleId);
            jdbcTemplate.execute("INSERT INTO sys_role_perm (role_id, perm_id) SELECT " + roleId + ", perm_id FROM sys_permission");
            System.out.println("权限分配成功");

            System.out.println("\n=== 验证数据 ===");
            System.out.println("角色权限关联:");
            jdbcTemplate.queryForList("SELECT * FROM sys_role_perm WHERE role_id = " + roleId).forEach(System.out::println);

            System.out.println("\n管理员角色关联:");
            jdbcTemplate.queryForList("SELECT * FROM t_admin_role WHERE admin_id = " + adminId).forEach(System.out::println);

            System.out.println("\n=== 修复完成 ===");

        } catch (Exception e) {
            System.err.println("修复失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
