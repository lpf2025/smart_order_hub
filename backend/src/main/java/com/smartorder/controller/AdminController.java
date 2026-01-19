package com.smartorder.controller;

import com.smartorder.common.Result;
import com.smartorder.dto.AdminRoleSaveDTO;
import com.smartorder.entity.Admin;
import com.smartorder.entity.SysRole;
import com.smartorder.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    private final AdminService adminService;
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @GetMapping("/list")
    public Result<List<Admin>> getAdminList() {
        List<Admin> adminList = adminService.getAdminList();
        return Result.success(adminList);
    }
    
    @GetMapping("/detail")
    public Result<Admin> getAdminDetail(@RequestParam Long id) {
        Admin admin = adminService.getAdminDetail(id);
        if (admin != null) {
            return Result.success(admin);
        } else {
            return Result.error("管理员不存在");
        }
    }
    
    @PostMapping("/add")
    public Result<Void> addAdmin(@RequestBody Admin admin) {
        boolean success = adminService.addAdmin(admin);
        if (success) {
            return Result.success(null, "管理员添加成功");
        } else {
            return Result.error("管理员添加失败");
        }
    }
    
    @PostMapping("/update")
    public Result<Void> updateAdmin(@RequestBody Admin admin) {
        boolean success = adminService.updateAdmin(admin);
        if (success) {
            return Result.success(null, "管理员更新成功");
        } else {
            return Result.error("管理员更新失败");
        }
    }
    
    @PostMapping("/delete")
    public Result<Void> deleteAdmin(@RequestBody Admin admin) {
        boolean success = adminService.deleteAdmin(admin.getId());
        if (success) {
            return Result.success(null, "管理员删除成功");
        } else {
            return Result.error("管理员删除失败");
        }
    }
    
    @PostMapping("/role/save")
    public Result<Void> saveAdminRoles(@RequestBody AdminRoleSaveDTO dto) {
        boolean success = adminService.saveAdminRoles(dto);
        if (success) {
            return Result.success(null, "角色配置成功");
        } else {
            return Result.error("角色配置失败");
        }
    }
}