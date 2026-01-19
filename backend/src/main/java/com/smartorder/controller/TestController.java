package com.smartorder.controller;

import com.smartorder.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/tables")
    public Result<List<Map<String, Object>>> getTables() {
        try {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
            return Result.success(tables);
        } catch (Exception e) {
            return Result.error("查询表失败: " + e.getMessage());
        }
    }

    @GetMapping("/merchant/count")
    public Result<Integer> getMerchantCount() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_merchant", Integer.class);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("查询门店数量失败: " + e.getMessage());
        }
    }

    @GetMapping("/dish/count")
    public Result<Integer> getDishCount() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_dish", Integer.class);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("查询菜品数量失败: " + e.getMessage());
        }
    }
}