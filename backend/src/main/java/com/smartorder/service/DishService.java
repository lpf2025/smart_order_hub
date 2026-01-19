package com.smartorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartorder.entity.Dish;
import java.util.List;
import java.util.Map;

public interface DishService extends IService<Dish> {
    
    List<Dish> getDishesByMerchantId(Long merchantId);
    
    List<Dish> getDishesByMerchantId(Long merchantId, String name, String category, Integer status);
    
    List<Dish> getDishesByCategory(Long merchantId, String category);
    
    List<String> getCategories(Long merchantId);
    
    List<Dish> getHotDishes(Long merchantId, int limit);
    
    Map<String, Object> getDishSalesStatistics(Long merchantId, Long dishId);
    
    void loadDishesSalesStatistics(List<Dish> dishes, Long merchantId);
}
