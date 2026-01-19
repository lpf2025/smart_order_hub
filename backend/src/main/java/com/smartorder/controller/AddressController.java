package com.smartorder.controller;

import com.smartorder.common.Result;
import com.smartorder.entity.Address;
import com.smartorder.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
@CrossOrigin
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @PostMapping("/list")
    public Result<List<Address>> list(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        List<Address> addresses = addressService.getAddressListByUserId(userId);
        return Result.success(addresses);
    }
    
    @PostMapping("/get")
    public Result<Address> get(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        Address address = addressService.getById(id);
        return Result.success(address);
    }
    
    @PostMapping("/default")
    public Result<Address> getDefault(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Address address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }
    
    @PostMapping("/add")
    public Result<Address> add(@RequestBody Address address) {
        Address createdAddress = addressService.createAddress(address);
        return Result.success(createdAddress);
    }
    
    @PostMapping("/update")
    public Result<Address> update(@RequestBody Address address) {
        Address updatedAddress = addressService.updateAddress(address);
        return Result.success(updatedAddress);
    }
    
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        addressService.deleteAddress(id);
        return Result.success(null);
    }
    
    @PostMapping("/setDefault")
    public Result<Void> setDefault(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        Address address = addressService.getById(id);
        if (address != null) {
            addressService.setDefaultAddress(id, address.getUserId());
        }
        return Result.success(null);
    }
}
