package com.smartorder;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@SpringBootTest
public class DatabaseInitTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void initDatabase() throws Exception {
        String sqlFile = "init_database.sql";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(sqlFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }

            String[] statements = sqlBuilder.toString().split(";");
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    try {
                        jdbcTemplate.execute(trimmed);
                        System.out.println("执行SQL成功: " + trimmed.substring(0, Math.min(50, trimmed.length())));
                    } catch (Exception e) {
                        System.err.println("执行SQL失败: " + trimmed.substring(0, Math.min(50, trimmed.length())));
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("数据库初始化完成");

        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
