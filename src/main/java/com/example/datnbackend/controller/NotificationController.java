package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.notification.NotificationCreateRequest;
import com.example.datnbackend.dto.post.PostUpdateTypeRequest;
import com.example.datnbackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping(value = "/type")
    ResponseEntity<Object> getAllTypeNotification(){
        try{
            return ResponseEntity.ok(notificationService.getAllTypeNotification());
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping(value = "/admin")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> createNotification(@RequestBody NotificationCreateRequest requestBody){
        try{
            notificationService.createNotification(requestBody);
            return new ResponseEntity<>(new MainResponse(true, "Xoá thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/admin/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> getNotificationDetailForAdmin(@PathVariable Long id){
        try{
            return ResponseEntity.ok(notificationService.getNotificationDetailForAdmin(id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/business/{id}")
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> getNotificationDetailForBusiness(@PathVariable Long id){
        try{
            return ResponseEntity.ok(notificationService.getNotificationDetailForBusiness(id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/admin/list")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> getNotificationDescriptionForAdmin(@RequestParam Integer page,
                                                              @RequestParam Integer size,
                                                              @RequestParam(value = "userId", required = false) Long userId,
                                                              @RequestParam(value = "typeId", required = false) Long typeId,
                                                              @RequestParam(value = "byMe", required = false) Boolean byMe){
        try{
            return ResponseEntity.ok(notificationService.getNotificationDescriptionForAdmin(page, size, userId, typeId, byMe));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/business/list")
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> getNotificationDescriptionForBusiness(@RequestParam Integer page,
                                                                 @RequestParam Integer size,
                                                                 @RequestParam(value = "typeId", required = false) Long typeId,
                                                                 @RequestParam(value = "viewed", required = false) Boolean viewed){
        try{
            return ResponseEntity.ok(notificationService.getNotificationDescriptionForBusiness(page, size,typeId, viewed));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping(value = "/admin")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> deleteNotificationCreated(@RequestBody List<Long> ids){
        try{
            notificationService.deleteNotificationCreated(ids);
            return new ResponseEntity<>(new MainResponse(true, "Xoá thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
