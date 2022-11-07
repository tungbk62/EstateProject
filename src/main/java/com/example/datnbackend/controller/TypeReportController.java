package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.type.TypeReportRequest;
import com.example.datnbackend.service.TypeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/type-report")
public class TypeReportController {
    @Autowired
    TypeReportService typeReportService;

    @PostMapping(value = "/admin")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> createTypeReport(@RequestBody TypeReportRequest requestBody){
        try{
            return ResponseEntity.ok(typeReportService.createTypeReport(requestBody));
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/public")
    ResponseEntity<Object> getAllTypeReport(@RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer size){
        try{
            return ResponseEntity.ok(typeReportService.getAllTypeReport(page, size));
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/admin/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> updateTypeReport(@PathVariable(value = "id") Long id,
                                            @RequestBody TypeReportRequest requestBody){
        try{
            return ResponseEntity.ok(typeReportService.updateTypeReport(id, requestBody));
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping(value = "/admin")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> deleteTypeReport(@RequestBody List<Long> ids){
        try{
            typeReportService.deleteTypeReport(ids);
            return new ResponseEntity<>(new MainResponse(true, "Xoá thành công"), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
