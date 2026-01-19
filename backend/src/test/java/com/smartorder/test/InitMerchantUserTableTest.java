package com.smartorder.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class InitMerchantUserTableTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    public void createMerchantUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `merchant_user` (" +
                     "`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID'," +
                     "`wx_openid` varchar(100) NOT NULL COMMENT '微信小程序openid'," +
                     "`wx_nickname` varchar(50) DEFAULT NULL COMMENT '微信昵称'," +
                     "`mobile` varchar(11) DEFAULT NULL COMMENT '手机号'," +
                     "`merchant_id` bigint DEFAULT NULL COMMENT '绑定商户ID'," +
                     "`has_delivery_perm` tinyint(1) DEFAULT 0 COMMENT '是否拥有发货权限（0=否，1=是）'," +
                     "`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                     "`update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                     "PRIMARY KEY (`id`)," +
                     "UNIQUE KEY `uk_wx_openid` (`wx_openid`)," +
                     "KEY `idx_merchant_id` (`merchant_id`)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家用户表'";
        
        try {
            jdbcTemplate.execute(sql);
            System.out.println("商家用户表创建成功");
        } catch (Exception e) {
            System.err.println("商家用户表创建失败: " + e.getMessage());
        }
    }
    
    @Test
    public void updatePermissions() {
        try {
            String checkSql = "SELECT COUNT(*) FROM sys_permission WHERE perm_code = 'system:merchant:user'";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            
            if (count == 0) {
                String insertMenuSql = "INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time) " +
                                       "VALUES ('商家用户管理', 'system:merchant:user', 1, 1, 2, NOW())";
                jdbcTemplate.update(insertMenuSql);
                System.out.println("商家用户管理菜单创建成功");
                
                Long menuId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                
                String[] buttonPerms = {
                    "INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time) VALUES ('商家用户列表查看', 'system:merchant:user:view', " + menuId + ", 2, 1, NOW())",
                    "INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time) VALUES ('商家用户编辑', 'system:merchant:user:edit', " + menuId + ", 2, 2, NOW())",
                    "INSERT INTO sys_permission (perm_name, perm_code, parent_id, menu_type, sort_order, create_time) VALUES ('发货权限配置', 'system:merchant:user:delivery:perm', " + menuId + ", 2, 3, NOW())"
                };
                
                for (String permSql : buttonPerms) {
                    jdbcTemplate.update(permSql);
                }
                System.out.println("商家用户管理按钮权限创建成功");
                
                Long roleId = jdbcTemplate.queryForObject("SELECT role_id FROM sys_role WHERE role_name = '超级管理员'", Long.class);
                
                String[] rolePerms = {
                    "INSERT INTO sys_role_perm (role_id, perm_id) VALUES (" + roleId + ", " + menuId + ")",
                    "INSERT INTO sys_role_perm (role_id, perm_id) VALUES (" + roleId + ", " + (menuId + 1) + ")",
                    "INSERT INTO sys_role_perm (role_id, perm_id) VALUES (" + roleId + ", " + (menuId + 2) + ")",
                    "INSERT INTO sys_role_perm (role_id, perm_id) VALUES (" + roleId + ", " + (menuId + 3) + ")"
                };
                
                for (String rolePermSql : rolePerms) {
                    jdbcTemplate.update(rolePermSql);
                }
                System.out.println("超级管理员权限绑定成功");
            } else {
                System.out.println("商家用户管理菜单已存在");
            }
            
            String updateUserPermSql = "UPDATE sys_permission SET perm_name = '客户管理', perm_code = 'system:customer' WHERE perm_code = 'user:list'";
            jdbcTemplate.update(updateUserPermSql);
            System.out.println("用户管理更新为客户管理成功");
            
        } catch (Exception e) {
            System.err.println("权限更新失败: " + e.getMessage());
        }
    }
}
