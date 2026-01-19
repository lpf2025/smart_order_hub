package com.smartorder.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OrderItemDTO {
    private Long dishId;
    private String specName;
    private Integer quantity;
    private BigDecimal price;
    
    public Long getDishId() {
        return dishId;
    }
    
    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }
    
    public String getSpecName() {
        return specName;
    }
    
    public void setSpecName(String specName) {
        this.specName = specName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
