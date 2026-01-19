package com.smartorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartorder.entity.User;
import com.smartorder.mapper.UserMapper;
import com.smartorder.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public User getUserByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return getOne(wrapper);
    }
    
    @Override
    public User registerUser(String openid, String nickname, String avatarUrl) {
        User user = new User();
        user.setOpenid(openid);
        user.setNickname(nickname);
        user.setAvatarUrl(avatarUrl);
        save(user);
        return user;
    }
}
