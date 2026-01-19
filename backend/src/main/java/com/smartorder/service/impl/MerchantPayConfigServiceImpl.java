package com.smartorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartorder.dto.MerchantPayConfigDTO;
import com.smartorder.dto.MerchantPayConfigTestDTO;
import com.smartorder.entity.MerchantPayConfig;
import com.smartorder.mapper.MerchantPayConfigMapper;
import com.smartorder.service.MerchantPayConfigService;
import org.springframework.stereotype.Service;

@Service
public class MerchantPayConfigServiceImpl implements MerchantPayConfigService {
    
    private final MerchantPayConfigMapper merchantPayConfigMapper;
    
    public MerchantPayConfigServiceImpl(MerchantPayConfigMapper merchantPayConfigMapper) {
        this.merchantPayConfigMapper = merchantPayConfigMapper;
    }
    
    @Override
    public MerchantPayConfig getByMerchantId(Long merchantId) {
        LambdaQueryWrapper<MerchantPayConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantPayConfig::getMerchantId, merchantId);
        return merchantPayConfigMapper.selectOne(wrapper);
    }
    
    @Override
    public boolean saveOrUpdate(MerchantPayConfigDTO dto) {
        MerchantPayConfig existingConfig = getByMerchantId(dto.getMerchantId());
        
        if (existingConfig != null) {
            existingConfig.setSupportPayTypes(dto.getSupportPayTypes());
            existingConfig.setWechatConfig(dto.getWechatConfig());
            existingConfig.setAlipayConfig(dto.getAlipayConfig());
            return merchantPayConfigMapper.updateById(existingConfig) > 0;
        } else {
            MerchantPayConfig newConfig = new MerchantPayConfig();
            newConfig.setMerchantId(dto.getMerchantId());
            newConfig.setSupportPayTypes(dto.getSupportPayTypes());
            newConfig.setWechatConfig(dto.getWechatConfig());
            newConfig.setAlipayConfig(dto.getAlipayConfig());
            return merchantPayConfigMapper.insert(newConfig) > 0;
        }
    }
    
    @Override
    public boolean testConfig(MerchantPayConfigTestDTO dto) {
        MerchantPayConfig config = getByMerchantId(dto.getMerchantId());
        
        if (config == null) {
            return false;
        }
        
        String payType = dto.getPayType();
        
        if ("wechat".equals(payType)) {
            String wechatConfig = config.getWechatConfig();
            return wechatConfig != null && !wechatConfig.isEmpty();
        } else if ("alipay".equals(payType)) {
            String alipayConfig = config.getAlipayConfig();
            return alipayConfig != null && !alipayConfig.isEmpty();
        }
        
        return false;
    }
}
