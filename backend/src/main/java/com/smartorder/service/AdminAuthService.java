package com.smartorder.service;

import com.smartorder.dto.AdminLoginDTO;
import com.smartorder.dto.AdminLoginVO;

public interface AdminAuthService {
    AdminLoginVO login(AdminLoginDTO dto);
    
    void logout(String token);
}
