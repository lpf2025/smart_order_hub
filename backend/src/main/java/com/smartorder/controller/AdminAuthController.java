package com.smartorder.controller;

import com.smartorder.common.Result;
import com.smartorder.dto.AdminLoginDTO;
import com.smartorder.dto.AdminLoginVO;
import com.smartorder.service.AdminAuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {
    
    private final AdminAuthService adminAuthService;
    
    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }
    
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO dto) {
        AdminLoginVO vo = adminAuthService.login(dto);
        return Result.success(vo);
    }
    
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        adminAuthService.logout(token);
        return Result.success(null, "退出成功");
    }
}
