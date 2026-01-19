package com.smartorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartorder.entity.User;

public interface UserService extends IService<User> {
    
    User getUserByOpenid(String openid);
    
    User registerUser(String openid, String nickname, String avatarUrl);
}
