package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.security.*;
import com.example.datnbackend.dto.user.ForgetPasswordChangeRequest;
import com.example.datnbackend.dto.user.ForgetPasswordOTPRequest;
import com.example.datnbackend.dto.user.ForgetPasswordRequest;
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
    ResponseEntity<Object> signup(@Valid @RequestBody UserSignupRequest requestBody){
        try{
            userService.signup(requestBody);
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
    ResponseEntity<Object> signupAdmin(@Valid @RequestBody UserSignupAdminRequest requestBody){
        try{
            userService.signupAdmin(requestBody);
            return new ResponseEntity<>(new MainResponse(true, "Đăng ký thành công"), HttpStatus.OK);
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

    @PostMapping(value = "/public/password/forget")
    ResponseEntity<Object> forgetPasswordRequest(@RequestBody ForgetPasswordRequest requestBody){
        try{
            userService.forgetPasswordRequest(requestBody);
            return ResponseEntity.ok(new MainResponse(true, "Mã OTP đã được gửi tới email của bạn"));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping(value = "/public/password/forget/otp")
    ResponseEntity<Object> forgetPasswordOTPRequest(@RequestBody ForgetPasswordOTPRequest requestBody){
        try{
            return ResponseEntity.ok(userService.forgetPasswordOTPRequest(requestBody));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping(value = "/public/password/forget/change")
    ResponseEntity<Object> forgetPasswordChangeRequest(@RequestBody ForgetPasswordChangeRequest requestBody){
        try{
            userService.forgetPasswordChangeRequest(requestBody);
            return ResponseEntity.ok(new MainResponse(true, "Thay mật khẩu thành công"));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
