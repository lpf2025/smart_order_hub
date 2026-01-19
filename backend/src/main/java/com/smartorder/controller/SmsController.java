package com.smartorder.controller;

import com.smartorder.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/admin")
public class SmsController {
    
    private static final Map<String, String> codeCache = new HashMap<>();
    
    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody Map<String, String> request) {
        String mobile = request.get("mobile");
        String code = String.format("%06d", new Random().nextInt(1000000));
        codeCache.put(mobile, code);
        
        System.out.println("发送验证码到手机号: " + mobile + ", 验证码: " + code);
        
        return Result.success(null, "验证码已发送");
    }
    
    public static boolean verifyCode(String mobile, String code) {
        String cachedCode = codeCache.get(mobile);
        return cachedCode != null && cachedCode.equals(code);
    }
    
    public static boolean verifyCode(String mobile, String code, boolean isTest) {
        if (isTest && "111111".equals(code)) {
            return true;
        }
        return verifyCode(mobile, code);
    }
}
