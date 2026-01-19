package com.smartorder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("merchant_pay_config")
public class MerchantPayConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long merchantId;
    
    private String supportPayTypes;
    
    private String wechatConfig;
    
    private String alipayConfig;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
    
    public String getSupportPayTypes() {
        return supportPayTypes;
    }
    
    public void setSupportPayTypes(String supportPayTypes) {
        this.supportPayTypes = supportPayTypes;
    }
    
    public String getWechatConfig() {
        return wechatConfig;
    }
    
    public void setWechatConfig(String wechatConfig) {
        this.wechatConfig = wechatConfig;
    }
    
    public String getAlipayConfig() {
        return alipayConfig;
    }
    
    public void setAlipayConfig(String alipayConfig) {
        this.alipayConfig = alipayConfig;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
