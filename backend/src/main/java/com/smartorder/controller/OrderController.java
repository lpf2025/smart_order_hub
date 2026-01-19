package com.smartorder.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartorder.common.Result;
import com.smartorder.dto.OrderCreateDTO;
import com.smartorder.entity.Address;
import com.smartorder.entity.Delivery;
import com.smartorder.entity.Order;
import com.smartorder.entity.OrderItem;
import com.smartorder.service.AddressService;
import com.smartorder.service.DeliveryService;
import com.smartorder.service.OrderService;
import com.smartorder.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderItemService orderItemService;
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private DeliveryService deliveryService;
    
    @PostMapping("/create")
    public Result<Order> createOrder(
            @Valid @RequestBody OrderCreateDTO orderCreateDTO,
            @RequestHeader("X-User-Id") Long userId) {
        Order order = orderService.createOrder(orderCreateDTO, userId);
        return Result.success(order);
    }
    
    @PostMapping("/orderNo")
    public Result<Map<String, Object>> getOrderByOrderNo(@RequestBody Map<String, String> request) {
        Order order = orderService.getOrderByOrderNo(request.get("orderNo"));
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(order.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItems", orderItems);
        
        return Result.success(result);
    }
    
    @PostMapping("/get")
    public Result<Map<String, Object>> getOrder(@RequestBody Map<String, Long> request) {
        Order order = orderService.getOrderByIdWithMerchantName(request.get("id"));
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(order.getId());
        
        Address address = null;
        if (order.getAddressId() != null) {
            address = addressService.getById(order.getAddressId());
        }
        
        Delivery delivery = null;
        if (order.getMerchantUserId() != null) {
            delivery = deliveryService.getOne(new LambdaQueryWrapper<Delivery>()
                .eq(Delivery::getOrderId, order.getId())
                .eq(Delivery::getMerchantUserId, order.getMerchantUserId())
                .last("LIMIT 1"));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItems", orderItems);
        result.put("address", address);
        result.put("delivery", delivery);
        
        return Result.success(result);
    }
    
    @PostMapping("/user")
    public Result<Page<Order>> getUserOrders(@RequestBody UserOrderRequest request) {
        Page<Order> pageParam = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, request.getUserId())
                .orderByDesc(Order::getCreatedAt);
        Page<Order> result = orderService.page(pageParam, wrapper);
        return Result.success(result);
    }
    
    @PostMapping("/merchant")
    public Result<Page<Map<String, Object>>> getMerchantOrders(@RequestBody MerchantOrderRequest request) {
        Page<Order> pageParam = new Page<>(request.getPage(), request.getSize());
        Page<Order> orderPage = orderService.getMerchantOrdersWithMerchantName(request.getMerchantId(), request.getStatus(), pageParam);
        
        Page<Map<String, Object>> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        
        for (Order order : orderPage.getRecords()) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("orderNo", order.getOrderNo());
            orderMap.put("merchantId", order.getMerchantId());
            orderMap.put("merchantName", order.getMerchantName());
            orderMap.put("userId", order.getUserId());
            orderMap.put("orderType", order.getOrderType());
            orderMap.put("deliveryType", order.getDeliveryType());
            orderMap.put("reserveTime", order.getReserveTime());
            orderMap.put("tableNo", order.getTableNo());
            orderMap.put("addressId", order.getAddressId());
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("status", order.getStatus());
            orderMap.put("payStatus", order.getPayStatus());
            orderMap.put("remark", order.getRemark());
            orderMap.put("createdAt", order.getCreatedAt());
            orderMap.put("updatedAt", order.getUpdatedAt());
            orderMap.put("deleted", order.getDeleted());
            
            Address address = null;
            if (order.getAddressId() != null) {
                address = addressService.getById(order.getAddressId());
            }
            orderMap.put("address", address);
            
            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(order.getId());
            orderMap.put("items", orderItems);
            
            records.add(orderMap);
        }
        
        result.setRecords(records);
        return Result.success(result);
    }
    
    @PostMapping("/merchant-user")
    public Result<Page<Map<String, Object>>> getMerchantUserOrders(@RequestBody MerchantUserOrderRequest request) {
        Page<Order> pageParam = new Page<>(request.getPage(), request.getSize());
        Page<Order> orderPage = orderService.getMerchantUserOrdersWithMerchantName(request.getMerchantUserId(), request.getStatus(), pageParam);
        
        Page<Map<String, Object>> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        
        for (Order order : orderPage.getRecords()) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("orderNo", order.getOrderNo());
            orderMap.put("merchantId", order.getMerchantId());
            orderMap.put("merchantName", order.getMerchantName());
            orderMap.put("merchantUserId", order.getMerchantUserId());
            orderMap.put("userId", order.getUserId());
            orderMap.put("orderType", order.getOrderType());
            orderMap.put("deliveryType", order.getDeliveryType());
            orderMap.put("reserveTime", order.getReserveTime());
            orderMap.put("tableNo", order.getTableNo());
            orderMap.put("addressId", order.getAddressId());
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("status", order.getStatus());
            orderMap.put("payStatus", order.getPayStatus());
            orderMap.put("remark", order.getRemark());
            orderMap.put("createdAt", order.getCreatedAt());
            orderMap.put("updatedAt", order.getUpdatedAt());
            orderMap.put("deleted", order.getDeleted());
            
            Address address = null;
            if (order.getAddressId() != null) {
                address = addressService.getById(order.getAddressId());
            }
            orderMap.put("address", address);
            
            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(order.getId());
            orderMap.put("items", orderItems);
            
            records.add(orderMap);
        }
        
        result.setRecords(records);
        return Result.success(result);
    }
    
    @PostMapping("/status")
    public Result<Boolean> updateOrderStatus(@RequestBody Map<String, Object> request) {
        boolean result = orderService.updateOrderStatus(Long.valueOf(request.get("id").toString()), Integer.valueOf(request.get("status").toString()));
        return Result.success(result);
    }
    
    @PostMapping("/accept")
    public Result<Boolean> acceptOrder(@RequestBody AcceptOrderRequest request) {
        boolean result = orderService.acceptOrder(request.getOrderId(), request.getMerchantUserId(), request.getDeliveryUserId());
        return Result.success(result);
    }
    
    @PostMapping("/cancel")
    public Result<Boolean> cancelOrder(@RequestBody Map<String, Long> request) {
        boolean result = orderService.cancelOrder(request.get("id"));
        return Result.success(result);
    }
    
    @PostMapping("/pay")
    public Result<Map<String, Object>> payOrder(@RequestBody Map<String, Long> request) {
        Long orderId = request.get("id");
        Order order = orderService.getById(orderId);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (order.getPayStatus() == 1) {
            return Result.error("订单已支付");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("nonceStr", "mock_nonce_str_" + System.currentTimeMillis());
        result.put("package", "prepay_id=mock_prepay_id_" + orderId);
        result.put("signType", "MD5");
        result.put("paySign", "mock_sign_" + orderId);
        
        return Result.success(result);
    }
    
    @PostMapping("/pay/callback")
    public Result<Boolean> payCallback(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        Order order = orderService.getById(orderId);
        
        if (order != null) {
            order.setPayStatus(1);
            orderService.updateById(order);
        }
        
        return Result.success(true);
    }
    
    public static class UserOrderRequest {
        private Long userId;
        private Integer page = 1;
        private Integer size = 10;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public Integer getPage() {
            return page;
        }
        
        public void setPage(Integer page) {
            this.page = page;
        }
        
        public Integer getSize() {
            return size;
        }
        
        public void setSize(Integer size) {
            this.size = size;
        }
    }
    
    public static class MerchantOrderRequest {
        private Long merchantId;
        private Integer page = 1;
        private Integer size = 10;
        private Integer status;
        
        public Long getMerchantId() {
            return merchantId;
        }
        
        public void setMerchantId(Long merchantId) {
            this.merchantId = merchantId;
        }
        
        public Integer getPage() {
            return page;
        }
        
        public void setPage(Integer page) {
            this.page = page;
        }
        
        public Integer getSize() {
            return size;
        }
        
        public void setSize(Integer size) {
            this.size = size;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
    }
    
    public static class MerchantUserOrderRequest {
        private Long merchantUserId;
        private Integer page = 1;
        private Integer size = 10;
        private Integer status;
        
        public Long getMerchantUserId() {
            return merchantUserId;
        }
        
        public void setMerchantUserId(Long merchantUserId) {
            this.merchantUserId = merchantUserId;
        }
        
        public Integer getPage() {
            return page;
        }
        
        public void setPage(Integer page) {
            this.page = page;
        }
        
        public Integer getSize() {
            return size;
        }
        
        public void setSize(Integer size) {
            this.size = size;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
    }
    
    public static class AcceptOrderRequest {
        private Long orderId;
        private Long merchantUserId;
        private Long deliveryUserId;
        
        public Long getOrderId() {
            return orderId;
        }
        
        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
        
        public Long getMerchantUserId() {
            return merchantUserId;
        }
        
        public void setMerchantUserId(Long merchantUserId) {
            this.merchantUserId = merchantUserId;
        }
        
        public Long getDeliveryUserId() {
            return deliveryUserId;
        }
        
        public void setDeliveryUserId(Long deliveryUserId) {
            this.deliveryUserId = deliveryUserId;
        }
    }
}
