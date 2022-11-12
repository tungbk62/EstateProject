package com.example.datnbackend.service;

import com.example.datnbackend.dto.security.*;
import com.example.datnbackend.dto.user.UserDescriptionAdminResponse;
import com.example.datnbackend.dto.user.UserDetailAdminResponseRequest;
import com.example.datnbackend.dto.user.UserDetailRequest;
import com.example.datnbackend.dto.user.UserDetailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    ResponseEntity<?> signin(UserSigninRequest signinDTO);
    void signup(UserSignupRequest signupDTO, MultipartFile fileImage);
    SecurityResponse signupAdmin(UserSignupAdminRequest signupDTO);
    List<UserDescriptionAdminResponse> getUserDescriptionForAdmin(Integer page, Integer size, String type, String query);
    UserDetailResponse getUserDetail(Long id);
    UserDetailAdminResponseRequest getUserDetailForAdmin(Long id);
    UserDetailResponse updateUserDetail(Long id, UserDetailRequest requestBody, MultipartFile fileImage);
    void lockUserAccount(Long id, Boolean locked);
    void displayReviewUserAccount(Long id, Boolean display);
    void deleteMultipleUser(List<Long> ids);
    void changePassword(ChangePasswordRequest requestBody);
}
