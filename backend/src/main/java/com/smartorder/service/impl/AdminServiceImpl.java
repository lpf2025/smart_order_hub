package com.smartorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartorder.dto.AdminRoleSaveDTO;
import com.smartorder.entity.Admin;
import com.smartorder.entity.AdminRole;
import com.smartorder.entity.SysRole;
import com.smartorder.mapper.AdminMapper;
import com.smartorder.mapper.AdminRoleMapper;
import com.smartorder.mapper.SysRoleMapper;
import com.smartorder.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    
    private final AdminMapper adminMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    
    public AdminServiceImpl(AdminMapper adminMapper, AdminRoleMapper adminRoleMapper, SysRoleMapper sysRoleMapper) {
        this.adminMapper = adminMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
    }
    
    @Override
    public List<Admin> getAdminList() {
        List<Admin> admins = adminMapper.selectList(null);
        
        for (Admin admin : admins) {
            LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AdminRole::getAdminId, admin.getId());
            List<AdminRole> adminRoles = adminRoleMapper.selectList(wrapper);
            
            if (!adminRoles.isEmpty()) {
                List<Long> roleIds = adminRoles.stream()
                        .map(AdminRole::getRoleId)
                        .collect(Collectors.toList());
                
                LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
                roleWrapper.in(SysRole::getRoleId, roleIds);
                List<SysRole> roles = sysRoleMapper.selectList(roleWrapper);
                admin.setRoles(roles);
            }
        }
        
        return admins;
    }
    
    @Override
    public Admin getAdminDetail(Long id) {
        return adminMapper.selectById(id);
    }
    
    @Override
    public boolean addAdmin(Admin admin) {
        return adminMapper.insert(admin) > 0;
    }
    
    @Override
    public boolean updateAdmin(Admin admin) {
        return adminMapper.updateById(admin) > 0;
    }
    
    @Override
    @Transactional
    public boolean deleteAdmin(Long id) {
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, id);
        adminRoleMapper.delete(wrapper);
        
        return adminMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional
    public boolean saveAdminRoles(AdminRoleSaveDTO dto) {
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, dto.getAdminId());
        adminRoleMapper.delete(wrapper);
        
        for (Long roleId : dto.getRoleIds()) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(dto.getAdminId());
            adminRole.setRoleId(roleId);
            adminRoleMapper.insert(adminRole);
        }
        
        return true;
    }
}