package com.smartorder.dto;

import java.util.List;

public class PermissionVO {
    private Long permId;
    private String permName;
    private String permCode;
    private Long parentId;
    private Integer menuType;
    private Integer sortOrder;
    private List<PermissionVO> children;
    
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
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public List<PermissionVO> getChildren() {
        return children;
    }
    
    public void setChildren(List<PermissionVO> children) {
        this.children = children;
    }
}
