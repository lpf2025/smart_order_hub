package com.smartorder.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartorder.common.Result;
import com.smartorder.entity.MerchantUser;
import com.smartorder.mapper.MerchantUserMapper;
import com.smartorder.service.MerchantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/merchant/user")
@CrossOrigin
public class MerchantUserController {
    
    @Autowired
    private MerchantUserService merchantUserService;
    
    @Autowired
    private MerchantUserMapper merchantUserMapper;
    
    @PostMapping("/login")
    public Result<MerchantUser> login(@RequestBody MerchantUserLoginRequest request) {
        System.out.println("登录请求参数: openid=" + request.getOpenid() + ", merchantId=" + request.getMerchantId());
        
        String mobile = null;
        
        if (request.getPhoneCode() != null && !request.getPhoneCode().isEmpty()) {
            mobile = getPhoneNumberByCode(request.getPhoneCode());
        }
        
        MerchantUser user = merchantUserService.getByOpenid(request.getOpenid());
        System.out.println("查询用户结果: " + (user != null ? "找到用户, id=" + user.getId() : "未找到用户"));
        
        if (user == null) {
            user = merchantUserService.createOrUpdate(request.getOpenid(), request.getNickname(), request.getAvatarUrl(), request.getMerchantId(), mobile);
            System.out.println("创建新用户结果: " + (user != null ? "成功, id=" + user.getId() : "失败"));
        } else {
            user = merchantUserService.createOrUpdate(request.getOpenid(), null, null, null, mobile);
            System.out.println("更新用户结果: " + (user != null ? "成功, id=" + user.getId() : "失败"));
        }
        return Result.success(user);
    }
    
    @GetMapping("/list")
    public Result<Page<MerchantUser>> getMerchantUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long merchantId) {
        Page<MerchantUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MerchantUser> wrapper = new LambdaQueryWrapper<>();
        
        if (merchantId != null) {
            wrapper.eq(MerchantUser::getMerchantId, merchantId);
        }
        
        wrapper.orderByDesc(MerchantUser::getCreateTime);
        Page<MerchantUser> result = merchantUserMapper.selectPage(pageParam, wrapper);
        return Result.success(result);
    }
    
    @GetMapping("/detail")
    public Result<MerchantUser> getMerchantUserDetail(@RequestParam Long id) {
        MerchantUser user = merchantUserMapper.selectById(id);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }
    
    @PostMapping("/delivery/perm/config")
    public Result<?> configDeliveryPerm(@RequestBody MerchantUserPermDTO dto) {
        if (dto.getUserId() == null || dto.getHasDeliveryPerm() == null) {
            return Result.error("参数不能为空");
        }
        boolean success = merchantUserService.updateDeliveryPerm(dto.getUserId(), dto.getHasDeliveryPerm());
        if (success) {
            return Result.success(null, "权限配置成功");
        } else {
            return Result.error("权限配置失败");
        }
    }
    
    @PostMapping("/update")
    public Result<?> updateMerchantUser(@RequestBody MerchantUser user) {
        boolean success = merchantUserMapper.updateById(user) > 0;
        if (success) {
            return Result.success(null, "更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @PostMapping("/logout")
    public Result<?> logout(@RequestBody MerchantUserLogoutRequest request) {
        if (request.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        boolean success = merchantUserService.updateLastLogoutTime(request.getUserId());
        if (success) {
            return Result.success(null, "退出登录成功");
        } else {
            return Result.error("退出登录失败");
        }
    }
    
    public static class MerchantUserLogoutRequest {
        private Long userId;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
    
    public static class MerchantUserPermDTO {
        private Long userId;
        private Integer hasDeliveryPerm;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public Integer getHasDeliveryPerm() {
            return hasDeliveryPerm;
        }
        
        public void setHasDeliveryPerm(Integer hasDeliveryPerm) {
            this.hasDeliveryPerm = hasDeliveryPerm;
        }
    }
    
    public static class MerchantUserLoginRequest {
        private String openid;
        private String nickname;
        private String avatarUrl;
        private Long merchantId;
        private String phoneCode;
        
        public String getOpenid() {
            return openid;
        }
        
        public void setOpenid(String openid) {
            this.openid = openid;
        }
        
        public String getNickname() {
            return nickname;
        }
        
        public void setNickname(String nickname) {
            this.nickname = nickname;
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
        
        public String getPhoneCode() {
            return phoneCode;
        }
        
        public void setPhoneCode(String phoneCode) {
            this.phoneCode = phoneCode;
        }
    }
    
    private String getPhoneNumberByCode(String code) {
        try {
            return "13800138000";
        } catch (Exception e) {
            System.err.println("解密手机号失败: " + e.getMessage());
            return null;
        }
    }
}
