package com.smartorder.controller;

import com.smartorder.common.Result;
import com.smartorder.dto.RolePermSaveDTO;
import com.smartorder.dto.RolePermVO;
import com.smartorder.entity.SysRole;
import com.smartorder.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/role")
public class RoleController {
    
    private final RoleService roleService;
    
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    @GetMapping("/list")
    public Result<List<SysRole>> getRoleList() {
        List<SysRole> roleList = roleService.getRoleList();
        return Result.success(roleList);
    }
    
    @GetMapping("/detail")
    public Result<SysRole> getRoleDetail(@RequestParam Long roleId) {
        SysRole role = roleService.getRoleDetail(roleId);
        if (role != null) {
            return Result.success(role);
        } else {
            return Result.error("角色不存在");
        }
    }
    
    @PostMapping("/add")
    public Result<Void> addRole(@RequestBody SysRole role) {
        boolean success = roleService.addRole(role);
        if (success) {
            return Result.success(null, "角色添加成功");
        } else {
            return Result.error("角色添加失败");
        }
    }
    
    @PostMapping("/update")
    public Result<Void> updateRole(@RequestBody SysRole role) {
        boolean success = roleService.updateRole(role);
        if (success) {
            return Result.success(null, "角色更新成功");
        } else {
            return Result.error("角色更新失败");
        }
    }
    
    @PostMapping("/delete")
    public Result<Void> deleteRole(@RequestParam Long roleId) {
        boolean success = roleService.deleteRole(roleId);
        if (success) {
            return Result.success(null, "角色删除成功");
        } else {
            return Result.error("角色删除失败");
        }
    }
    
    @GetMapping("/perm/get")
    public Result<List<RolePermVO>> getRolePermissions(@RequestParam Long roleId) {
        List<RolePermVO> permList = roleService.getRolePermissions(roleId);
        return Result.success(permList);
    }
    
    @PostMapping("/perm/save")
    public Result<Void> saveRolePermissions(@RequestBody RolePermSaveDTO dto) {
        boolean success = roleService.saveRolePermissions(dto);
        if (success) {
            return Result.success(null, "权限保存成功");
        } else {
            return Result.error("权限保存失败");
        }
    }
}
