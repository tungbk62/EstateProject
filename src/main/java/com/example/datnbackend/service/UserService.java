package com.example.datnbackend.service;

import com.example.datnbackend.dto.security.*;
import com.example.datnbackend.dto.user.UserDescriptionAdminResponse;
import com.example.datnbackend.dto.user.UserDetailRequest;
import com.example.datnbackend.dto.user.UserDetailResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<?> signin(UserSigninRequest signinDTO);
    SecurityResponse signup(UserSignupRequest signupDTO);
    SecurityResponse signupAdmin(UserSignupAdminRequest signupDTO);
    List<UserDescriptionAdminResponse> getUserDescriptionForAdmin(Integer page, Integer size, String query);
    UserDetailResponse getUserDetail(Long id);
    UserDetailResponse getUserDetailForAdmin(Long id);
    UserDetailResponse updateUserDetail(Long id, UserDetailRequest requestBody);
    void lockUserAccount(Long id, Boolean locked);
    void displayReviewUserAccount(Long id, Boolean display);
    void deleteMultipleUser(List<Long> ids);
    void changePassword(ChangePasswordRequest requestBody);
}
