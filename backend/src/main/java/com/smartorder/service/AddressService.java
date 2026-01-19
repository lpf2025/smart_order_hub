package com.smartorder.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartorder.entity.Address;
import com.smartorder.mapper.AddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService extends ServiceImpl<AddressMapper, Address> {
    
    public List<Address> getAddressListByUserId(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .orderByDesc(Address::getIsDefault)
               .orderByDesc(Address::getCreatedAt);
        return list(wrapper);
    }
    
    public Address getDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .eq(Address::getIsDefault, true);
        return getOne(wrapper);
    }
    
    @Transactional
    public Address createAddress(Address address) {
        if (address.getIsDefault() != null && address.getIsDefault()) {
            clearDefaultAddress(address.getUserId());
        }
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        save(address);
        return address;
    }
    
    @Transactional
    public Address updateAddress(Address address) {
        if (address.getIsDefault() != null && address.getIsDefault()) {
            clearDefaultAddress(address.getUserId());
        }
        address.setUpdatedAt(LocalDateTime.now());
        updateById(address);
        return address;
    }
    
    @Transactional
    public void deleteAddress(Long id) {
        removeById(id);
    }
    
    @Transactional
    public void setDefaultAddress(Long id, Long userId) {
        clearDefaultAddress(userId);
        Address address = getById(id);
        if (address != null) {
            address.setIsDefault(true);
            address.setUpdatedAt(LocalDateTime.now());
            updateById(address);
        }
    }
    
    private void clearDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .eq(Address::getIsDefault, true);
        Address defaultAddress = getOne(wrapper);
        if (defaultAddress != null) {
            defaultAddress.setIsDefault(false);
            defaultAddress.setUpdatedAt(LocalDateTime.now());
            updateById(defaultAddress);
        }
    }
}
