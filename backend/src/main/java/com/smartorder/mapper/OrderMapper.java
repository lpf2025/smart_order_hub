package com.smartorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartorder.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    List<Order> getOrdersWithMerchantName(@Param("userId") Long userId, @Param("merchantId") Long merchantId, @Param("status") Integer status);
    
    Page<Order> getOrdersWithMerchantName(@Param("userId") Long userId, @Param("merchantId") Long merchantId, @Param("status") Integer status, @Param("page") Page<Order> page);
    
    Page<Order> getMerchantUserOrdersWithMerchantName(@Param("merchantUserId") Long merchantUserId, @Param("status") Integer status, @Param("page") Page<Order> page);
    
    Order getOrderByIdWithMerchantName(@Param("id") Long id);
    
    Map<String, Object> getSalesStatistics(@Param("merchantId") Long merchantId);
}
