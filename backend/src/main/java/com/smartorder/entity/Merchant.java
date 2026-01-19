package com.smartorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_merchant")
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String phone;
    
    private String address;
    
    private Integer status;
    
    private Integer serviceMode;
    
    private String serviceModes;
    
    private String businessHours;
    
    private String logoUrl;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal minDeliveryAmount;
    
    private BigDecimal minTakeoutAmount;
    
    private String deliveryTime;
    
    private String takeoutTime;
    
    private String takeawayDeliveryTypes;
    
    private String defaultDeliveryType;
    
    private String startTime;
    
    private String endTime;
    
    private Integer monthSales;
    
    private BigDecimal perCapita;
    
    private BigDecimal rating;
    
    private BigDecimal minOrder;
    
    private Integer canReserve;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getServiceMode() {
        return serviceMode;
    }
    
    public void setServiceMode(Integer serviceMode) {
        this.serviceMode = serviceMode;
    }
    
    public String getServiceModes() {
        return serviceModes;
    }
    
    public void setServiceModes(String serviceModes) {
        this.serviceModes = serviceModes;
    }
    
    public String getBusinessHours() {
        return businessHours;
    }
    
    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    
    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }
    
    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
    
    public BigDecimal getMinDeliveryAmount() {
        return minDeliveryAmount;
    }
    
    public void setMinDeliveryAmount(BigDecimal minDeliveryAmount) {
        this.minDeliveryAmount = minDeliveryAmount;
    }
    
    public BigDecimal getMinTakeoutAmount() {
        return minTakeoutAmount;
    }
    
    public void setMinTakeoutAmount(BigDecimal minTakeoutAmount) {
        this.minTakeoutAmount = minTakeoutAmount;
    }
    
    public String getDeliveryTime() {
        return deliveryTime;
    }
    
    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
    
    public String getTakeoutTime() {
        return takeoutTime;
    }
    
    public void setTakeoutTime(String takeoutTime) {
        this.takeoutTime = takeoutTime;
    }
    
    public String getTakeawayDeliveryTypes() {
        return takeawayDeliveryTypes;
    }
    
    public void setTakeawayDeliveryTypes(String takeawayDeliveryTypes) {
        this.takeawayDeliveryTypes = takeawayDeliveryTypes;
    }
    
    public String getDefaultDeliveryType() {
        return defaultDeliveryType;
    }
    
    public void setDefaultDeliveryType(String defaultDeliveryType) {
        this.defaultDeliveryType = defaultDeliveryType;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public Integer getMonthSales() {
        return monthSales;
    }
    
    public void setMonthSales(Integer monthSales) {
        this.monthSales = monthSales;
    }
    
    public BigDecimal getPerCapita() {
        return perCapita;
    }
    
    public void setPerCapita(BigDecimal perCapita) {
        this.perCapita = perCapita;
    }
    
    public BigDecimal getRating() {
        return rating;
    }
    
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
    
    public BigDecimal getMinOrder() {
        return minOrder;
    }
    
    public void setMinOrder(BigDecimal minOrder) {
        this.minOrder = minOrder;
    }
    
    public Integer getCanReserve() {
        return canReserve;
    }
    
    public void setCanReserve(Integer canReserve) {
        this.canReserve = canReserve;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Integer getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
