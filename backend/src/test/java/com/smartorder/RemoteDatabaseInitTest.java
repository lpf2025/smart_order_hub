package com.smartorder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class RemoteDatabaseInitTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void initRemoteDatabase() {
        System.out.println("=== 开始初始化远程数据库 ===");

        try {
            System.out.println("1. 插入权限数据...");
            jdbcTemplate.execute("INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order) VALUES " +
                "('系统管理', 'system', 0, 1, 1), " +
                "('角色管理', 'system:role', 1, 1, 1), " +
                "('商家管理', 'merchant:list', 0, 1, 2), " +
                "('菜品管理', 'dish:list', 0, 1, 3), " +
                "('订单管理', 'order:list', 0, 1, 4), " +
                "('用户管理', 'user:list', 0, 1, 5) " +
                "ON DUPLICATE KEY UPDATE perm_name=VALUES(perm_name)");
            System.out.println("权限数据插入成功");

            System.out.println("2. 插入超级管理员角色...");
            jdbcTemplate.execute("INSERT INTO sys_role (role_name, role_desc) VALUES " +
                "('超级管理员', '拥有所有权限') " +
                "ON DUPLICATE KEY UPDATE role_name=VALUES(role_name)");
            System.out.println("角色插入成功");

            System.out.println("3. 插入超级管理员账号...");
            jdbcTemplate.execute("INSERT INTO t_admin (username, mobile) VALUES " +
                "('超级管理员', '13681978701') " +
                "ON DUPLICATE KEY UPDATE username=VALUES(username)");
            System.out.println("管理员账号插入成功");

            System.out.println("4. 为超级管理员分配角色...");
            jdbcTemplate.execute("INSERT IGNORE INTO t_admin_role (admin_id, role_id) VALUES (1, 1)");
            System.out.println("角色分配成功");

            System.out.println("5. 为超级管理员角色分配所有权限...");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_role_perm (role_id, perm_id) SELECT 1, perm_id FROM sys_permission");
            System.out.println("权限分配成功");

            System.out.println("=== 远程数据库初始化完成 ===");

            System.out.println("\n=== 验证数据 ===");
            System.out.println("管理员账号:");
            jdbcTemplate.queryForList("SELECT * FROM t_admin").forEach(System.out::println);
            System.out.println("\n角色:");
            jdbcTemplate.queryForList("SELECT * FROM sys_role").forEach(System.out::println);
            System.out.println("\n权限:");
            jdbcTemplate.queryForList("SELECT * FROM sys_permission").forEach(System.out::println);
            System.out.println("\n角色权限关联:");
            jdbcTemplate.queryForList("SELECT * FROM sys_role_perm").forEach(System.out::println);

        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
