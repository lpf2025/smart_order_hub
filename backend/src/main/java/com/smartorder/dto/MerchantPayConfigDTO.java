package com.smartorder.dto;

public class MerchantPayConfigDTO {
    private Long merchantId;
    private String supportPayTypes;
    private String wechatConfig;
    private String alipayConfig;
    
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
}
