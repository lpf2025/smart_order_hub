package com.smartorder.controller;

import com.smartorder.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/test")
@CrossOrigin
public class TestDataController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostMapping("/initDishes")
    public Result<String> initDishes() {
        try {
            ClassPathResource resource = new ClassPathResource("test-dishes.sql");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );
            
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }
            reader.close();
            
            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (sql.trim().length() > 0) {
                    jdbcTemplate.execute(sql.trim());
                }
            }
            
            return Result.success("测试菜品数据初始化成功");
        } catch (Exception e) {
            return Result.error("初始化失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/initOrders")
    public Result<String> initOrders() {
        try {
            ClassPathResource resource = new ClassPathResource("test-orders.sql");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );
            
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }
            reader.close();
            
            String[] sqlStatements = sqlBuilder.toString().split(";");
            for (String sql : sqlStatements) {
                if (sql.trim().length() > 0) {
                    jdbcTemplate.execute(sql.trim());
                }
            }
            
            return Result.success("测试订单数据初始化成功");
        } catch (Exception e) {
            return Result.error("初始化失败: " + e.getMessage());
        }
    }
}
