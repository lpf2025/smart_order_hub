package com.smartorder.controller;

import com.smartorder.common.Result;
import com.smartorder.entity.Merchant;
import com.smartorder.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant")
@CrossOrigin
public class MerchantController {
    
    @Autowired
    private MerchantService merchantService;
    
    @PostMapping("/get")
    public Result<Merchant> getMerchant(@RequestBody java.util.Map<String, Long> request) {
        Merchant merchant = merchantService.getMerchantById(request.get("id"));
        return Result.success(merchant);
    }
    
    @PostMapping("/list")
    public Result<List<Merchant>> getAllMerchants(@RequestBody(required = false) java.util.Map<String, Object> request) {
        String name = request != null ? (String) request.get("name") : null;
        List<Merchant> merchants = merchantService.searchMerchants(name);
        return Result.success(merchants);
    }
    
    @PostMapping("/save")
    public Result<Boolean> saveMerchant(@RequestBody Merchant merchant) {
        return Result.success(merchantService.saveOrUpdate(merchant));
    }
    
    @PostMapping("/delete")
    public Result<Boolean> deleteMerchant(@RequestBody java.util.Map<String, Long> request) {
        return Result.success(merchantService.removeById(request.get("id")));
    }
    
    @PostMapping("/sales/statistics")
    public Result<java.util.Map<String, Object>> getSalesStatistics(@RequestBody java.util.Map<String, Long> request) {
        Long merchantId = request.get("merchantId");
        java.util.Map<String, Object> statistics = merchantService.getSalesStatistics(merchantId);
        return Result.success(statistics);
    }
}
