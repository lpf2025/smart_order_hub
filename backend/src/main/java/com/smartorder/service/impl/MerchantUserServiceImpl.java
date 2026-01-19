package com.smartorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartorder.entity.MerchantUser;
import com.smartorder.mapper.MerchantUserMapper;
import com.smartorder.service.MerchantUserService;
import org.springframework.stereotype.Service;

@Service
public class MerchantUserServiceImpl implements MerchantUserService {
    
    private final MerchantUserMapper merchantUserMapper;
    
    public MerchantUserServiceImpl(MerchantUserMapper merchantUserMapper) {
        this.merchantUserMapper = merchantUserMapper;
    }
    
    @Override
    public MerchantUser getById(Long id) {
        return merchantUserMapper.selectById(id);
    }
    
    @Override
    public MerchantUser getByOpenid(String openid) {
        LambdaQueryWrapper<MerchantUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantUser::getWxOpenid, openid);
        return merchantUserMapper.selectOne(wrapper);
    }
    
    @Override
    public MerchantUser createOrUpdate(String openid, String nickname, String avatarUrl, Long merchantId, String mobile) {
        LambdaQueryWrapper<MerchantUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantUser::getWxOpenid, openid);
        MerchantUser existingUser = merchantUserMapper.selectOne(wrapper);
        
        if (existingUser != null) {
            existingUser.setUpdateTime(java.time.LocalDateTime.now());
            existingUser.setLastLoginTime(java.time.LocalDateTime.now());
            if (mobile != null && !mobile.isEmpty()) {
                existingUser.setMobile(mobile);
            }
            merchantUserMapper.updateById(existingUser);
            return existingUser;
        } else {
            MerchantUser newUser = new MerchantUser();
            newUser.setWxOpenid(openid);
            newUser.setWxNickname(nickname);
            newUser.setMerchantId(merchantId);
            newUser.setHasDeliveryPerm(0);
            newUser.setLastLoginTime(java.time.LocalDateTime.now());
            if (mobile != null && !mobile.isEmpty()) {
                newUser.setMobile(mobile);
            }
            merchantUserMapper.insert(newUser);
            return newUser;
        }
    }
    
    @Override
    public boolean updateDeliveryPerm(Long userId, Integer hasDeliveryPerm) {
        MerchantUser user = merchantUserMapper.selectById(userId);
        if (user != null) {
            user.setHasDeliveryPerm(hasDeliveryPerm);
            return merchantUserMapper.updateById(user) > 0;
        }
        return false;
    }
    
    @Override
    public boolean updateLastLogoutTime(Long userId) {
        MerchantUser user = merchantUserMapper.selectById(userId);
        if (user != null) {
            user.setLastLogoutTime(java.time.LocalDateTime.now());
            return merchantUserMapper.updateById(user) > 0;
        }
        return false;
    }
}
