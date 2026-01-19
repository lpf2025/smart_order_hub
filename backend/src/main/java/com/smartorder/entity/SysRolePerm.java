package com.smartorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role_perm")
public class SysRolePerm {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long roleId;
    
    private Long permId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRoleId() {
        return roleId;
    }
    
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
    
    public Long getPermId() {
        return permId;
    }
    
    public void setPermId(Long permId) {
        this.permId = permId;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
