package com.smartorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartorder.entity.Delivery;

public interface DeliveryService extends IService<Delivery> {
    
    Delivery createDelivery(Long orderId, Long merchantUserId);
}
