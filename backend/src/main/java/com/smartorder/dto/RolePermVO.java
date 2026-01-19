package com.smartorder.dto;

import java.util.List;

public class RolePermVO {
    private Long permId;
    private String permName;
    private String permCode;
    private Long parentId;
    private Integer menuType;
    private Boolean checked;
    private List<RolePermVO> children;
    
    public Long getPermId() {
        return permId;
    }
    
    public void setPermId(Long permId) {
        this.permId = permId;
    }
    
    public String getPermName() {
        return permName;
    }
    
    public void setPermName(String permName) {
        this.permName = permName;
    }
    
    public String getPermCode() {
        return permCode;
    }
    
    public void setPermCode(String permCode) {
        this.permCode = permCode;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public Integer getMenuType() {
        return menuType;
    }
    
    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }
    
    public Boolean getChecked() {
        return checked;
    }
    
    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
    
    public List<RolePermVO> getChildren() {
        return children;
    }
    
    public void setChildren(List<RolePermVO> children) {
        this.children = children;
    }
}
