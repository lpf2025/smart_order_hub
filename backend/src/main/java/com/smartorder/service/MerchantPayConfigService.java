package com.smartorder.service;

import com.smartorder.dto.MerchantPayConfigDTO;
import com.smartorder.dto.MerchantPayConfigTestDTO;
import com.smartorder.entity.MerchantPayConfig;

public interface MerchantPayConfigService {
    MerchantPayConfig getByMerchantId(Long merchantId);
    
    boolean saveOrUpdate(MerchantPayConfigDTO dto);
    
    boolean testConfig(MerchantPayConfigTestDTO dto);
}
