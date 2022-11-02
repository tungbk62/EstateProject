package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.security.SecurityResponse;
import com.example.datnbackend.dto.security.UserSigninRequest;
import com.example.datnbackend.dto.security.UserSignupAdminRequest;
import com.example.datnbackend.dto.security.UserSignupRequest;
import com.example.datnbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping(value = "/signup")
    ResponseEntity<SecurityResponse> signup(@Valid @RequestBody UserSignupRequest signupDTO){
        return ResponseEntity.ok(userService.signup(signupDTO));
    }

    @PostMapping(value = "/signin")
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

}
