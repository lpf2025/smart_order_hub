package com.smartorder.service;

import com.smartorder.entity.MerchantUser;

public interface MerchantUserService {
    
    MerchantUser getById(Long id);
    
    MerchantUser getByOpenid(String openid);
    
    MerchantUser createOrUpdate(String openid, String nickname, String avatarUrl, Long merchantId, String mobile);
    
    boolean updateDeliveryPerm(Long userId, Integer hasDeliveryPerm);
    
    boolean updateLastLogoutTime(Long userId);
}
