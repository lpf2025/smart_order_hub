package com.smartorder.service;

import com.smartorder.dto.RolePermSaveDTO;
import com.smartorder.dto.RolePermVO;
import com.smartorder.entity.SysRole;

import java.util.List;

public interface RoleService {
    List<SysRole> getRoleList();
    
    SysRole getRoleDetail(Long roleId);
    
    boolean addRole(SysRole role);
    
    boolean updateRole(SysRole role);
    
    boolean deleteRole(Long roleId);
    
    List<RolePermVO> getRolePermissions(Long roleId);
    
    boolean saveRolePermissions(RolePermSaveDTO dto);
}
