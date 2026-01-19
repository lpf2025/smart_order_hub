package com.smartorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartorder.dto.OrderCreateDTO;
import com.smartorder.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService extends IService<Order> {
    
    Order createOrder(OrderCreateDTO orderCreateDTO, Long userId);
    
    Order getOrderByOrderNo(String orderNo);
    
    Order getOrderByIdWithMerchantName(Long id);
    
    List<Order> getOrdersWithMerchantName(Long userId, Long merchantId, Integer status);
    
    Page<Order> getMerchantOrdersWithMerchantName(Long merchantId, Integer status, Page<Order> page);
    
    Page<Order> getMerchantUserOrdersWithMerchantName(Long merchantUserId, Integer status, Page<Order> page);
    
    boolean updateOrderStatus(Long orderId, Integer status);
    
    boolean acceptOrder(Long orderId, Long merchantUserId, Long deliveryUserId);
    
    boolean cancelOrder(Long orderId);
    
    BigDecimal calculateOrderTotalAmount(OrderCreateDTO orderCreateDTO);
}
