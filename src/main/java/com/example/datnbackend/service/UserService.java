package com.example.datnbackend.service;

import com.example.datnbackend.dto.security.*;
import com.example.datnbackend.dto.user.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    ResponseEntity<?> signin(UserSigninRequest signinDTO);
    void signup(UserSignupRequest signupDTO);
    void signupAdmin(UserSignupAdminRequest signupDTO);
    List<UserDescriptionAdminResponse> getUserDescriptionForAdmin(Integer page, Integer size, String type, String query);
    UserDetailResponse getUserDetail();
    UserDetailAdminResponseRequest getUserDetailForAdmin(Long id);
    UserDetailResponse updateUserDetail(UserDetailRequest requestBody);
    void lockUserAccount(Long id, Boolean locked);
    void displayReviewUserAccount(Long id, Boolean display);
    void deleteMultipleUser(List<Long> ids);
    void changePassword(ChangePasswordRequest requestBody);
    void forgetPasswordRequest(ForgetPasswordRequest requestBody);
    ForgetPasswordOTPResponse forgetPasswordOTPRequest(ForgetPasswordOTPRequest requestBody);
    void forgetPasswordChangeRequest(ForgetPasswordChangeRequest requestBody);
    void logout();

}
