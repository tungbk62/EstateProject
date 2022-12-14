package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.user.UserDetailRequest;
import com.example.datnbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/admin")
    @Secured({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    ResponseEntity<Object> getUserDescriptionForAdmin(@RequestParam Integer page,
                                                      @RequestParam Integer size,
                                                      @RequestParam(required = false) String type,
                                                      @RequestParam(required = false) String query){
        try{
            return ResponseEntity.ok(userService.getUserDescriptionForAdmin(page, size, type, query));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping
    ResponseEntity<Object> getUserDetail(){
        try{
            return ResponseEntity.ok(userService.getUserDetail());
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/admin/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> getUserDetailForAdmin(@PathVariable(value = "id") Long id){
        try{
            return ResponseEntity.ok(userService.getUserDetailForAdmin(id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping
    ResponseEntity<Object> updateUserDetail(@RequestBody UserDetailRequest requestBody){
        try{
            return ResponseEntity.ok(userService.updateUserDetail(requestBody));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/admin/locked/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> lockUserAccount(@PathVariable(value = "id") Long id,
                                           @RequestParam Boolean locked){
        try{
            userService.lockUserAccount(id, locked);
            return new ResponseEntity<>(new MainResponse(true, "Change status of account successfully"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/admin/display-review/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> displayReviewUserAccount(@PathVariable(value = "id") Long id,
                                                    @RequestParam Boolean display){
        try{
            userService.displayReviewUserAccount(id, display);
            return new ResponseEntity<>(new MainResponse(true, "Change status of display review successfully"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("/admin")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> deleteMultipleUser(@RequestBody List<Long> ids){
        try{
            userService.deleteMultipleUser(ids);
            return new ResponseEntity<>(new MainResponse(true, "Delete all user successfully"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
