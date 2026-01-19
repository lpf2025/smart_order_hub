package com.smartorder.controller;

import com.smartorder.common.Result;
import com.smartorder.entity.User;
import com.smartorder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        User user = userService.getUserByOpenid(request.getOpenid());
        if (user == null) {
            user = userService.registerUser(request.getOpenid(), request.getNickname(), request.getAvatarUrl());
        }
        return Result.success(user);
    }
    
    @PostMapping("/get")
    public Result<User> getUser(@RequestBody java.util.Map<String, Long> request) {
        User user = userService.getById(request.get("id"));
        return Result.success(user);
    }
    
    @PostMapping("/list")
    public Result<java.util.List<User>> list() {
        return Result.success(userService.list());
    }
    
    @PostMapping("/customer/list")
    public Result<java.util.List<User>> customerList() {
        return Result.success(userService.list());
    }
    
    public static class LoginRequest {
        private String openid;
        private String nickname;
        private String avatarUrl;
        
        public String getOpenid() {
            return openid;
        }
        
        public void setOpenid(String openid) {
            this.openid = openid;
        }
        
        public String getNickname() {
            return nickname;
        }
        
        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
        
        public String getAvatarUrl() {
            return avatarUrl;
        }
        
        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
