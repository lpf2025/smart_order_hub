package com.smartorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartorder.entity.Dish;
import com.smartorder.entity.Merchant;
import com.smartorder.mapper.DishMapper;
import com.smartorder.service.DishService;
import com.smartorder.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    
    @Autowired
    private MerchantService merchantService;
    
    @Override
    public List<Dish> getDishesByMerchantId(Long merchantId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId)
                .orderByAsc(Dish::getSortOrder);
        return list(wrapper);
    }
    
    @Override
    public List<Dish> getDishesByMerchantId(Long merchantId, String name, String category, Integer status) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        
        if (merchantId != null) {
            wrapper.eq(Dish::getMerchantId, merchantId);
        }
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(Dish::getName, name);
        }
        
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Dish::getCategory, category);
        }
        
        if (status != null) {
            wrapper.eq(Dish::getStatus, status);
        }
        
        wrapper.orderByAsc(Dish::getSortOrder);
        List<Dish> dishes = list(wrapper);
        
        Map<Long, String> merchantNameMap = merchantService.list()
                .stream()
                .collect(Collectors.toMap(Merchant::getId, Merchant::getName));
        
        dishes.forEach(dish -> {
            if (dish.getMerchantId() != null) {
                dish.setMerchantName(merchantNameMap.get(dish.getMerchantId()));
            }
        });
        
        return dishes;
    }
    
    @Override
    public List<Dish> getDishesByCategory(Long merchantId, String category) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId)
                .eq(Dish::getCategory, category)
                .eq(Dish::getStatus, 1)
                .orderByAsc(Dish::getSortOrder);
        return list(wrapper);
    }
    
    @Override
    public List<String> getCategories(Long merchantId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId)
                .eq(Dish::getStatus, 1)
                .select(Dish::getCategory)
                .groupBy(Dish::getCategory);
        return list(wrapper).stream()
                .map(Dish::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Dish> getHotDishes(Long merchantId, int limit) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId)
                .eq(Dish::getStatus, 1)
                .orderByDesc(Dish::getSales)
                .last("LIMIT " + limit);
        return list(wrapper);
    }
    
    @Override
    public Map<String, Object> getDishSalesStatistics(Long merchantId, Long dishId) {
        return baseMapper.getDishSalesStatistics(merchantId, dishId);
    }
    
    @Override
    public void loadDishesSalesStatistics(List<Dish> dishes, Long merchantId) {
        if (dishes == null || dishes.isEmpty()) {
            return;
        }
        
        List<Long> dishIds = dishes.stream()
                .map(Dish::getId)
                .collect(Collectors.toList());
        
        if (dishIds.isEmpty()) {
            dishes.forEach(dish -> dish.setMonthSales(0));
            return;
        }
        
        try {
            List<Map<String, Object>> statisticsList = baseMapper.getBatchDishSalesStatistics(merchantId, dishIds);
            
            Map<Long, Integer> monthSalesMap = statisticsList.stream()
                    .collect(Collectors.toMap(
                            stat -> ((Number) stat.get("dishId")).longValue(),
                            stat -> {
                                Object monthQuantity = stat.get("monthQuantity");
                                return monthQuantity != null ? ((Number) monthQuantity).intValue() : 0;
                            }
                    ));
            
            dishes.forEach(dish -> {
                Integer monthSales = monthSalesMap.get(dish.getId());
                dish.setMonthSales(monthSales != null ? monthSales : 0);
            });
        } catch (Exception e) {
            dishes.forEach(dish -> dish.setMonthSales(0));
        }
    }
}
