package com.smartorder.service;

import com.smartorder.dto.AdminRoleSaveDTO;
import com.smartorder.entity.Admin;

import java.util.List;

public interface AdminService {
    List<Admin> getAdminList();
    
    Admin getAdminDetail(Long id);
    
    boolean addAdmin(Admin admin);
    
    boolean updateAdmin(Admin admin);
    
    boolean deleteAdmin(Long id);
    
    boolean saveAdminRoles(AdminRoleSaveDTO dto);
}