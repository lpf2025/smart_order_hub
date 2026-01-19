package com.smartorder.controller;

import com.smartorder.common.Result;
import com.smartorder.dto.MerchantPayConfigDTO;
import com.smartorder.dto.MerchantPayConfigTestDTO;
import com.smartorder.entity.MerchantPayConfig;
import com.smartorder.service.MerchantPayConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/pay/config")
public class MerchantPayConfigController {
    
    private final MerchantPayConfigService merchantPayConfigService;
    
    public MerchantPayConfigController(MerchantPayConfigService merchantPayConfigService) {
        this.merchantPayConfigService = merchantPayConfigService;
    }
    
    @GetMapping("/get")
    public Result<MerchantPayConfig> getPayConfig(@RequestParam Long merchantId) {
        MerchantPayConfig config = merchantPayConfigService.getByMerchantId(merchantId);
        return Result.success(config);
    }
    
    @PostMapping("/save")
    public Result<Void> savePayConfig(@RequestBody MerchantPayConfigDTO dto) {
        if (dto.getSupportPayTypes() == null || dto.getSupportPayTypes().trim().isEmpty()) {
            return Result.error("至少选择一种收款方式");
        }
        
        boolean success = merchantPayConfigService.saveOrUpdate(dto);
        if (success) {
            return Result.success(null, "配置保存成功");
        } else {
            return Result.error("配置保存失败");
        }
    }
    
    @PostMapping("/test")
    public Result<Void> testPayConfig(@RequestBody MerchantPayConfigTestDTO dto) {
        boolean isValid = merchantPayConfigService.testConfig(dto);
        if (isValid) {
            return Result.success(null, "配置有效，可正常收款");
        } else {
            return Result.error("配置无效，请检查参数");
        }
    }
}
