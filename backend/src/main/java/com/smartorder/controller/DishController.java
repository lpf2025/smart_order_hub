package com.smartorder.controller;

import com.smartorder.common.Result;
import com.smartorder.entity.Dish;
import com.smartorder.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dish")
@CrossOrigin
public class DishController {
    
    @Autowired
    private DishService dishService;
    
    @PostMapping("/list")
    public Result<java.util.List<Dish>> listDishes() {
        List<Dish> dishes = dishService.list();
        return Result.success(dishes);
    }
    
    @PostMapping("/merchant")
    public Result<java.util.List<Dish>> getDishesByMerchant(@RequestBody DishQueryRequest request) {
        List<Dish> dishes = dishService.getDishesByMerchantId(
            request.getMerchantId(), 
            request.getName(), 
            request.getCategory(), 
            request.getStatus()
        );
        dishService.loadDishesSalesStatistics(dishes, request.getMerchantId());
        return Result.success(dishes);
    }
    
    @PostMapping("/category")
    public Result<java.util.List<Dish>> getDishesByCategory(@RequestBody CategoryRequest request) {
        List<Dish> dishes = dishService.getDishesByCategory(request.getMerchantId(), request.getCategory());
        dishService.loadDishesSalesStatistics(dishes, request.getMerchantId());
        return Result.success(dishes);
    }
    
    @PostMapping("/categories")
    public Result<java.util.List<String>> getCategories(@RequestBody java.util.Map<String, Long> request) {
        List<String> categories = dishService.getCategories(request.get("merchantId"));
        return Result.success(categories);
    }
    
    @PostMapping("/get")
    public Result<Dish> getDish(@RequestBody java.util.Map<String, Long> request) {
        Dish dish = dishService.getById(request.get("id"));
        return Result.success(dish);
    }
    
    @PostMapping("/hot")
    public Result<java.util.List<Dish>> getHotDishes(@RequestBody java.util.Map<String, Long> request) {
        List<Dish> dishes = dishService.getHotDishes(request.get("merchantId"), 3);
        return Result.success(dishes);
    }
    
    @PostMapping("/save")
    public Result<Boolean> saveDish(@RequestBody Dish dish) {
        return Result.success(dishService.saveOrUpdate(dish));
    }
    
    @PostMapping("/delete")
    public Result<Boolean> deleteDish(@RequestBody java.util.Map<String, Long> request) {
        return Result.success(dishService.removeById(request.get("id")));
    }
    
    public static class CategoryRequest {
        private Long merchantId;
        private String category;
        
        public Long getMerchantId() {
            return merchantId;
        }
        
        public void setMerchantId(Long merchantId) {
            this.merchantId = merchantId;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
    }
    
    public static class DishQueryRequest {
        private Long merchantId;
        private String name;
        private String category;
        private Integer status;
        
        public Long getMerchantId() {
            return merchantId;
        }
        
        public void setMerchantId(Long merchantId) {
            this.merchantId = merchantId;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
