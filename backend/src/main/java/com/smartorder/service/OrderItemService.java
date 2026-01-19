package com.smartorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartorder.entity.OrderItem;
import java.util.List;

public interface OrderItemService extends IService<OrderItem> {
    
    List<OrderItem> getOrderItemsByOrderId(Long orderId);
}
