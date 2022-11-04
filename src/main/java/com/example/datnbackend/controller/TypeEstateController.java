package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.type.TypeEstateRequest;
import com.example.datnbackend.service.TypeEstateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/type-estate")
public class TypeEstateController {
    @Autowired
    TypeEstateService typeEstateService;

    @PostMapping(value = "/admin")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> createTypeEstate(@RequestBody TypeEstateRequest requestBody){
        try{
            return ResponseEntity.ok(typeEstateService.createTypeEstate(requestBody));
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping
    ResponseEntity<Object> getAllTypeEstate(@RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size){
        try{
            return ResponseEntity.ok(typeEstateService.getAllTypeEstate(page, size));
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/admin/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> updateTypeEstate(@PathVariable(value = "id") Long id,
                                            @RequestBody TypeEstateRequest requestBody){
        try{
            return ResponseEntity.ok(typeEstateService.updateTypeEstate(id, requestBody));
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping(value = "/admin")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> deleteTypeEstate(@RequestBody List<Long> ids){
        try{
            typeEstateService.deleteTypeEstate(ids);
            return new ResponseEntity<>(new MainResponse(true, "Xoá thành công"), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
