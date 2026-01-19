package com.smartorder.dto;

import java.util.List;

public class AdminRoleSaveDTO {
    private Long adminId;
    private List<Long> roleIds;
    
    public Long getAdminId() {
        return adminId;
    }
    
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
    
    public List<Long> getRoleIds() {
        return roleIds;
    }
    
    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}