package com.smartorder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class FixPermissionParentIdTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void fixPermissionParentId() {
        System.out.println("=== 修复权限parent_id ===");

        try {
            System.out.println("1. 获取系统管理权限ID...");
            Integer systemPermId = jdbcTemplate.queryForObject("SELECT perm_id FROM sys_permission WHERE perm_code = 'system'", Integer.class);
            System.out.println("系统管理权限ID: " + systemPermId);

            System.out.println("2. 更新角色管理的parent_id...");
            jdbcTemplate.execute("UPDATE sys_permission SET parent_id = " + systemPermId + " WHERE perm_code = 'system:role'");
            System.out.println("更新成功");

            System.out.println("\n=== 验证数据 ===");
            System.out.println("权限数据:");
            jdbcTemplate.queryForList("SELECT * FROM sys_permission ORDER BY perm_id").forEach(System.out::println);

            System.out.println("\n=== 修复完成 ===");

        } catch (Exception e) {
            System.err.println("修复失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
