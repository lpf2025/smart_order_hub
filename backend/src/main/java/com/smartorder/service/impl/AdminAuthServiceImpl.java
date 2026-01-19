package com.smartorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartorder.controller.SmsController;
import com.smartorder.dto.AdminInfoVO;
import com.smartorder.dto.AdminLoginDTO;
import com.smartorder.dto.AdminLoginVO;
import com.smartorder.dto.PermissionVO;
import com.smartorder.entity.Admin;
import com.smartorder.entity.AdminRole;
import com.smartorder.entity.SysPermission;
import com.smartorder.entity.SysRole;
import com.smartorder.entity.SysRolePerm;
import com.smartorder.mapper.AdminMapper;
import com.smartorder.mapper.AdminRoleMapper;
import com.smartorder.mapper.SysPermissionMapper;
import com.smartorder.mapper.SysRoleMapper;
import com.smartorder.mapper.SysRolePermMapper;
import com.smartorder.service.AdminAuthService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {
    
    private final AdminMapper adminMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermMapper sysRolePermMapper;
    private final AdminRoleMapper adminRoleMapper;
    
    public AdminAuthServiceImpl(AdminMapper adminMapper, SysRoleMapper sysRoleMapper, 
                                   SysPermissionMapper sysPermissionMapper, SysRolePermMapper sysRolePermMapper,
                                   AdminRoleMapper adminRoleMapper) {
        this.adminMapper = adminMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysPermissionMapper = sysPermissionMapper;
        this.sysRolePermMapper = sysRolePermMapper;
        this.adminRoleMapper = adminRoleMapper;
    }
    
    @Override
    public AdminLoginVO login(AdminLoginDTO dto) {
        if (!SmsController.verifyCode(dto.getMobile(), dto.getCode(), true)) {
            throw new RuntimeException("账户或验证码错误");
        }
        
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getMobile, dto.getMobile());
        Admin admin = adminMapper.selectOne(wrapper);
        
        if (admin == null) {
            throw new RuntimeException("账户或验证码错误");
        }
        
        String token = UUID.randomUUID().toString().replace("-", "");
        admin.setToken(token);
        adminMapper.updateById(admin);
        
        AdminLoginVO vo = new AdminLoginVO();
        vo.setToken(token);
        
        AdminInfoVO userInfo = new AdminInfoVO();
        userInfo.setId(admin.getId());
        userInfo.setUsername(admin.getUsername());
        userInfo.setMobile(admin.getMobile());
        
        LambdaQueryWrapper<AdminRole> adminRoleWrapper = new LambdaQueryWrapper<>();
        adminRoleWrapper.eq(AdminRole::getAdminId, admin.getId());
        List<AdminRole> adminRoles = adminRoleMapper.selectList(adminRoleWrapper);
        
        if (!adminRoles.isEmpty()) {
            List<Long> roleIds = adminRoles.stream()
                    .map(AdminRole::getRoleId)
                    .collect(Collectors.toList());
            
            LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.in(SysRole::getRoleId, roleIds);
            List<SysRole> roles = sysRoleMapper.selectList(roleWrapper);
            
            if (!roles.isEmpty()) {
                SysRole role = roles.get(0);
                userInfo.setRoleName(role.getRoleName());
            }
        }
        
        vo.setUserInfo(userInfo);
        vo.setMenuList(getMenuList(admin.getId()));
        vo.setButtonPermissions(getButtonPermissions(admin.getId()));
        
        return vo;
    }
    
    @Override
    public void logout(String token) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getToken, token);
        Admin admin = adminMapper.selectOne(wrapper);
        
        if (admin != null) {
            admin.setToken(null);
            adminMapper.updateById(admin);
        }
    }
    
    private List<PermissionVO> getMenuList(Long adminId) {
        LambdaQueryWrapper<AdminRole> adminRoleWrapper = new LambdaQueryWrapper<>();
        adminRoleWrapper.eq(AdminRole::getAdminId, adminId);
        List<AdminRole> adminRoles = adminRoleMapper.selectList(adminRoleWrapper);
        
        if (adminRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = adminRoles.stream()
                .map(AdminRole::getRoleId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<SysRolePerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRolePerm::getRoleId, roleIds);
        List<SysRolePerm> rolePerms = sysRolePermMapper.selectList(wrapper);
        
        if (rolePerms.isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<Long> permIds = rolePerms.stream()
                .map(SysRolePerm::getPermId)
                .collect(Collectors.toSet());
        
        List<SysPermission> permissions = sysPermissionMapper.selectBatchIds(new ArrayList<>(permIds));
        
        List<PermissionVO> menuList = permissions.stream()
                .filter(p -> p.getMenuType() == 1)
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return buildMenuTree(menuList, 0L);
    }
    
    private List<String> getButtonPermissions(Long adminId) {
        LambdaQueryWrapper<AdminRole> adminRoleWrapper = new LambdaQueryWrapper<>();
        adminRoleWrapper.eq(AdminRole::getAdminId, adminId);
        List<AdminRole> adminRoles = adminRoleMapper.selectList(adminRoleWrapper);
        
        if (adminRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> roleIds = adminRoles.stream()
                .map(AdminRole::getRoleId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<SysRolePerm> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRolePerm::getRoleId, roleIds);
        List<SysRolePerm> rolePerms = sysRolePermMapper.selectList(wrapper);
        
        if (rolePerms.isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<Long> permIds = rolePerms.stream()
                .map(SysRolePerm::getPermId)
                .collect(Collectors.toSet());
        
        List<SysPermission> permissions = sysPermissionMapper.selectBatchIds(new ArrayList<>(permIds));
        
        return permissions.stream()
                .filter(p -> p.getMenuType() == 2)
                .map(SysPermission::getPermCode)
                .collect(Collectors.toList());
    }
    
    private PermissionVO convertToVO(SysPermission perm) {
        PermissionVO vo = new PermissionVO();
        vo.setPermId(perm.getPermId());
        vo.setPermName(perm.getPermName());
        vo.setPermCode(perm.getPermCode());
        vo.setParentId(perm.getParentId());
        vo.setMenuType(perm.getMenuType());
        vo.setSortOrder(perm.getSortOrder());
        return vo;
    }
    
    private List<PermissionVO> buildMenuTree(List<PermissionVO> permissions, Long parentId) {
        List<PermissionVO> tree = new ArrayList<>();
        
        for (PermissionVO perm : permissions) {
            if (parentId.equals(perm.getParentId())) {
                List<PermissionVO> children = buildMenuTree(permissions, perm.getPermId());
                if (children == null || children.isEmpty()) {
                    perm.setChildren(new ArrayList<>());
                } else {
                    perm.setChildren(children);
                }
                tree.add(perm);
            }
        }
        
        tree.sort(Comparator.comparing(PermissionVO::getSortOrder));
        return tree;
    }
}