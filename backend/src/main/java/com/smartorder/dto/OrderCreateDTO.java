package com.smartorder.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class OrderCreateDTO {
    private Long merchantId;
    private Integer orderType;
    private String deliveryType;
    private LocalDateTime reserveTime;
    private String tableNo;
    private Long addressId;
    private String remark;
    private String payMethod;
    private List<OrderItemDTO> orderItems;
    
    public Long getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
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
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getPayMethod() {
        return payMethod;
    }
    
    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
    
    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
