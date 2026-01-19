package com.smartorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartorder.entity.Merchant;
import java.util.List;
import java.util.Map;

public interface MerchantService extends IService<Merchant> {
    
    Merchant getMerchantById(Long id);
    
    List<Merchant> getAllActiveMerchants();
    
    List<Merchant> searchMerchants(String name);
    
    Map<String, Object> getSalesStatistics(Long merchantId);
}
