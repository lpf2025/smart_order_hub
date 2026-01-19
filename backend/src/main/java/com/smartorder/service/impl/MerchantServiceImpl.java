package com.smartorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartorder.entity.Merchant;
import com.smartorder.mapper.MerchantMapper;
import com.smartorder.mapper.OrderMapper;
import com.smartorder.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Override
    public Merchant getMerchantById(Long id) {
        return getById(id);
    }
    
    @Override
    public List<Merchant> getAllActiveMerchants() {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getStatus, 1);
        return list(wrapper);
    }
    
    @Override
    public List<Merchant> searchMerchants(String name) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Merchant::getName, name.trim());
        }
        
        wrapper.orderByDesc(Merchant::getCreatedAt);
        List<Merchant> merchants = list(wrapper);
        
        for (Merchant merchant : merchants) {
            try {
                Map<String, Object> statistics = orderMapper.getSalesStatistics(merchant.getId());
                if (statistics != null) {
                    Object monthOrdersObj = statistics.get("monthOrders");
                    if (monthOrdersObj != null) {
                        merchant.setMonthSales(((Number) monthOrdersObj).intValue());
                    } else {
                        merchant.setMonthSales(0);
                    }
                } else {
                    merchant.setMonthSales(0);
                }
            } catch (Exception e) {
                merchant.setMonthSales(0);
            }
        }
        
        return merchants;
    }
    
    @Override
    public Map<String, Object> getSalesStatistics(Long merchantId) {
        Map<String, Object> statistics = orderMapper.getSalesStatistics(merchantId);
        
        if (statistics == null) {
            statistics = new java.util.HashMap<>();
            statistics.put("totalOrders", 0);
            statistics.put("totalAmount", 0);
            statistics.put("monthOrders", 0);
            statistics.put("monthAmount", 0);
            statistics.put("todayOrders", 0);
            statistics.put("todayAmount", 0);
        }
        
        Integer monthOrders = (Integer) statistics.get("monthOrders");
        if (monthOrders != null) {
            Merchant merchant = getById(merchantId);
            if (merchant != null) {
                merchant.setMonthSales(monthOrders);
                updateById(merchant);
            }
        }
        
        return statistics;
    }
}
