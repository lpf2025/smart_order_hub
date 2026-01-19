package com.smartorder.dto;

import java.util.List;

public class AdminLoginVO {
    private String token;
    private AdminInfoVO userInfo;
    private List<PermissionVO> menuList;
    private List<String> buttonPermissions;
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public AdminInfoVO getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo(AdminInfoVO userInfo) {
        this.userInfo = userInfo;
    }
    
    public List<PermissionVO> getMenuList() {
        return menuList;
    }
    
    public void setMenuList(List<PermissionVO> menuList) {
        this.menuList = menuList;
    }
    
    public List<String> getButtonPermissions() {
        return buttonPermissions;
    }
    
    public void setButtonPermissions(List<String> buttonPermissions) {
        this.buttonPermissions = buttonPermissions;
    }
}
