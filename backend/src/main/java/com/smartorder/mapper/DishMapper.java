package com.smartorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartorder.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    
    Map<String, Object> getDishSalesStatistics(@Param("merchantId") Long merchantId, @Param("dishId") Long dishId);
    
    List<Map<String, Object>> getBatchDishSalesStatistics(@Param("merchantId") Long merchantId, @Param("dishIds") List<Long> dishIds);
}
