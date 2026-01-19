package com.smartorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long merchantId;
    
    private Long merchantUserId;
    
    private Long deliveryUserId;
    
    @TableField(exist = false)
    private String merchantName;
    
    private Long userId;
    
    private Integer orderType;
    
    private String deliveryType;
    
    private LocalDateTime reserveTime;
    
    private String tableNo;
    
    private Long addressId;
    
    private BigDecimal totalAmount;
    
    private Integer status;
    
    private Integer payStatus;
    
    private String payMethod;
    
    private String deliveryNo;
    
    private String remark;
    
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
    
    public String getMerchantName() {
        return merchantName;
    }
    
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public Long getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Integer getOrderType() {
        return orderType;
    }
    
    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
    
    public String getDeliveryType() {
        return deliveryType;
    }
    
    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }
    
    public LocalDateTime getReserveTime() {
        return reserveTime;
    }
    
    public void setReserveTime(LocalDateTime reserveTime) {
        this.reserveTime = reserveTime;
    }
    
    public String getTableNo() {
        return tableNo;
    }
    
    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }
    
    public Long getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getPayStatus() {
        return payStatus;
    }
    
    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }
    
    public String getPayMethod() {
        return payMethod;
    }
    
    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
    
    public String getDeliveryNo() {
        return deliveryNo;
    }
    
    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
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
