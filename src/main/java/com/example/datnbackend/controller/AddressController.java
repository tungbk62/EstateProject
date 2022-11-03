package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/address")
public class AddressController {
    @Autowired
    AddressService addressService;

    @GetMapping(value = "/province")
    ResponseEntity<Object> getAllProvince(){
        try{
            return ResponseEntity.ok(addressService.getAllProvince());
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
    @GetMapping(value = "/province/{id}")
    ResponseEntity<Object> getProvinceDetail(@PathVariable(value = "id") Long id){
        try{
            return ResponseEntity.ok(addressService.getProvinceDetail(id));
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
