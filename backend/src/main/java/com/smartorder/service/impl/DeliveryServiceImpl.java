package com.smartorder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartorder.entity.Delivery;
import com.smartorder.entity.MerchantUser;
import com.smartorder.mapper.DeliveryMapper;
import com.smartorder.service.DeliveryService;
import com.smartorder.service.MerchantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryServiceImpl extends ServiceImpl<DeliveryMapper, Delivery> implements DeliveryService {
    
    @Autowired
    private MerchantUserService merchantUserService;
    
    @Override
    public Delivery createDelivery(Long orderId, Long merchantUserId) {
        MerchantUser merchantUser = merchantUserService.getById(merchantUserId);
        
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setMerchantUserId(merchantUserId);
        delivery.setDeliveryStatus(1);
        
        if (merchantUser != null) {
            delivery.setDeliveryName(merchantUser.getWxNickname());
            delivery.setDeliveryPhone(merchantUser.getMobile());
        }
        
        save(delivery);
        return delivery;
    }
}
