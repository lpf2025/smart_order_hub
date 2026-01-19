package com.smartorder.controller;

import com.smartorder.common.Result;
import com.smartorder.entity.MerchantUser;
import com.smartorder.service.MerchantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mini/merchant")
@CrossOrigin
public class MiniMerchantController {
    
    @Autowired
    private MerchantUserService merchantUserService;
    
    @GetMapping("/delivery/perm/check")
    public Result<?> checkDeliveryPerm(@RequestParam String openid) {
        MerchantUser user = merchantUserService.getByOpenid(openid);
        if (user == null) {
            return Result.error("用户未绑定商户");
        }
        return Result.success(user.getHasDeliveryPerm() == 1);
    }
}
