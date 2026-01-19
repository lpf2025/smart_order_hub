package com.smartorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("merchant_user")
public class MerchantUser {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String wxOpenid;
    
    private String wxNickname;
    
    private String mobile;
    
    private String avatarUrl;
    
    private Long merchantId;
    
    private Integer hasDeliveryPerm;
    
    private LocalDateTime lastLoginTime;
    
    private LocalDateTime lastLogoutTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getWxOpenid() {
        return wxOpenid;
    }
    
    public void setWxOpenid(String wxOpenid) {
        this.wxOpenid = wxOpenid;
    }
    
    public String getWxNickname() {
        return wxNickname;
    }
    
    public void setWxNickname(String wxNickname) {
        this.wxNickname = wxNickname;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public Long getMerchantId() {
        return merchantId;
    }
    
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }
    
    public Integer getHasDeliveryPerm() {
        return hasDeliveryPerm;
    }
    
    public void setHasDeliveryPerm(Integer hasDeliveryPerm) {
        this.hasDeliveryPerm = hasDeliveryPerm;
    }
    
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public LocalDateTime getLastLogoutTime() {
        return lastLogoutTime;
    }
    
    public void setLastLogoutTime(LocalDateTime lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
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
