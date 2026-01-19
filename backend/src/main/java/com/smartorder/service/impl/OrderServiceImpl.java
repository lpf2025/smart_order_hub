package com.smartorder.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartorder.dto.OrderCreateDTO;
import com.smartorder.dto.OrderItemDTO;
import com.smartorder.entity.Delivery;
import com.smartorder.entity.Dish;
import com.smartorder.entity.Order;
import com.smartorder.entity.OrderItem;
import com.smartorder.mapper.OrderMapper;
import com.smartorder.service.DeliveryService;
import com.smartorder.service.DishService;
import com.smartorder.service.OrderItemService;
import com.smartorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private DishService dishService;
    
    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private DeliveryService deliveryService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderCreateDTO orderCreateDTO, Long userId) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setMerchantId(orderCreateDTO.getMerchantId());
        order.setUserId(userId);
        order.setOrderType(orderCreateDTO.getOrderType());
        order.setDeliveryType(orderCreateDTO.getDeliveryType());
        order.setReserveTime(orderCreateDTO.getReserveTime());
        order.setTableNo(orderCreateDTO.getTableNo());
        order.setAddressId(orderCreateDTO.getAddressId());
        order.setRemark(orderCreateDTO.getRemark());
        order.setPayMethod(orderCreateDTO.getPayMethod() != null ? orderCreateDTO.getPayMethod() : "wechat");
        order.setDeliveryNo(generateDeliveryNo());
        order.setStatus(1);
        order.setPayStatus(0);
        
        BigDecimal totalAmount = calculateOrderTotalAmount(orderCreateDTO);
        order.setTotalAmount(totalAmount);
        
        save(order);
        
        List<OrderItemDTO> orderItems = orderCreateDTO.getOrderItems();
        for (OrderItemDTO itemDTO : orderItems) {
            Dish dish = dishService.getById(itemDTO.getDishId());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(itemDTO.getDishId());
            orderItem.setDishName(dish.getName());
            orderItem.setDishImage(dish.getImageUrl());
            orderItem.setSpecName(itemDTO.getSpecName());
            orderItem.setSpecPrice(itemDTO.getPrice() != null ? itemDTO.getPrice() : BigDecimal.ZERO);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(dish.getPrice());
            orderItem.setSubtotal(dish.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
            
            orderItemService.save(orderItem);
        }
        
        return order;
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return getOne(wrapper);
    }
    
    @Override
    public Order getOrderByIdWithMerchantName(Long id) {
        return baseMapper.getOrderByIdWithMerchantName(id);
    }
    
    @Override
    public List<Order> getOrdersWithMerchantName(Long userId, Long merchantId, Integer status) {
        return baseMapper.getOrdersWithMerchantName(userId, merchantId, status);
    }
    
    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order> getMerchantOrdersWithMerchantName(Long merchantId, Integer status, com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order> page) {
        return baseMapper.getOrdersWithMerchantName(null, merchantId, status, page);
    }
    
    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order> getMerchantUserOrdersWithMerchantName(Long merchantUserId, Integer status, com.baomidou.mybatisplus.extension.plugins.pagination.Page<Order> page) {
        return baseMapper.getMerchantUserOrdersWithMerchantName(merchantUserId, status, page);
    }
    
    @Override
    public boolean updateOrderStatus(Long orderId, Integer status) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(status);
        return updateById(order);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptOrder(Long orderId, Long merchantUserId, Long deliveryUserId) {
        Order order = getById(orderId);
        if (order == null) {
            return false;
        }
        
        if (order.getStatus() != 1) {
            return false;
        }
        
        order.setMerchantUserId(merchantUserId);
        order.setDeliveryUserId(deliveryUserId);
        order.setStatus(2);
        boolean updated = updateById(order);
        
        if (updated) {
            deliveryService.createDelivery(orderId, merchantUserId);
        }
        
        return updated;
    }
    
    @Override
    public boolean cancelOrder(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(6);
        return updateById(order);
    }
    
    @Override
    public BigDecimal calculateOrderTotalAmount(OrderCreateDTO orderCreateDTO) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderItemDTO itemDTO : orderCreateDTO.getOrderItems()) {
            Dish dish = dishService.getById(itemDTO.getDishId());
            if (dish != null) {
                BigDecimal itemPrice = dish.getPrice().multiply(new BigDecimal(itemDTO.getQuantity()));
                if (itemDTO.getPrice() != null) {
                    itemPrice = itemDTO.getPrice().multiply(new BigDecimal(itemDTO.getQuantity()));
                }
                totalAmount = totalAmount.add(itemPrice);
            }
        }
        
        return totalAmount;
    }
    
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + IdUtil.randomUUID().substring(0, 8).toUpperCase();
    }
    
    private String generateDeliveryNo() {
        return "DEL" + System.currentTimeMillis() + IdUtil.randomUUID().substring(0, 6).toUpperCase();
    }
}
