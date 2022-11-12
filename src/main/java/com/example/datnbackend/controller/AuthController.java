package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.security.*;
import com.example.datnbackend.entity.RoleEntity;
import com.example.datnbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping(value = "/public/signup")
    ResponseEntity<Object> signup(@Valid @RequestPart UserSignupRequest signupDTO,
                                  @RequestPart(value = "file", required = false) MultipartFile file){
        try{
            userService.signup(signupDTO, file);
            return new ResponseEntity<>(new MainResponse(true, "Đăng ký thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping(value = "/public/signin")
    ResponseEntity<?> signin(@Valid @RequestBody UserSigninRequest signinDTO){
        return userService.signin(signinDTO);
    }

    @PostMapping(value = "/signup-admin")
    @Secured("ROLE_SUPER_ADMIN")
    ResponseEntity<Object> signupAdmin(@Valid @RequestBody UserSignupAdminRequest signupDTO){
        try{
            return ResponseEntity.ok(userService.signupAdmin(signupDTO));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/password")
    ResponseEntity<Object> changePassword(@RequestBody ChangePasswordRequest requestBody){
        try{
            userService.changePassword(requestBody);
            return ResponseEntity.ok(new MainResponse(true, "Change password successfully"));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
